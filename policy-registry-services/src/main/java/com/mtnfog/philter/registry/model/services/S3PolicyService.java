package com.mtnfog.philter.registry.model.services;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.mtnfog.philter.registry.model.PolicyService;
import com.mtnfog.philter.registry.model.exceptions.BadRequestException;
import com.mtnfog.philter.registry.model.exceptions.InternalServerErrorException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Implementation of {@link PolicyService} that is backed by S3.
 */
public class S3PolicyService implements PolicyService {

    private static final Logger LOGGER = LogManager.getLogger(S3PolicyService.class);

    private Properties applicationProperties;

    private final AmazonS3 s3Client;
    private final String bucket;
    private final String prefix;

    public S3PolicyService(Properties applicationProperties, boolean testing) {

        // Initialize the S3 client.
        this.bucket = applicationProperties.getProperty("policies.store.s3.bucket");
        this.prefix = applicationProperties.getProperty("policies.store.s3.prefix");
        final String region = applicationProperties.getProperty("policies.s3.store.region", "us-east-1");

        LOGGER.info("Looking for policies in s3://{}{}", bucket, prefix);

        if(testing) {

            final AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration("http://localhost:8001", "us-west-2");

            this.s3Client = AmazonS3ClientBuilder
                    .standard()
                    .withPathStyleAccessEnabled(true)
                    .withEndpointConfiguration(endpoint)
                    .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                    .build();

            this.s3Client.createBucket(bucket);

        } else {

            // Only permits credentials via the standard channels.
            this.s3Client = AmazonS3ClientBuilder
                    .standard()
                    .withCredentials(new DefaultAWSCredentialsProviderChain())
                    .withRegion(region)
                    .build();

        }

    }

    @Override
    public List<String> get() {

        final List<String> names = new LinkedList<>();

        try {

            ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucket).withPrefix(prefix);
            ListObjectsV2Result result;

            do {

                result = s3Client.listObjectsV2(req);

                for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {

                    final S3Object fullObject = s3Client.getObject(new GetObjectRequest(bucket, objectSummary.getKey()));
                    final String json = IOUtils.toString(fullObject.getObjectContent(), StandardCharsets.UTF_8);

                    fullObject.close();

                    final JSONObject object = new JSONObject(json);
                    final String name = object.getString("name");

                    names.add(name);
                    LOGGER.debug("Added policy named {}", name);

                }

                // If there are more than maxKeys keys in the bucket, get a continuation token and list the next objects.
                final String token = result.getNextContinuationToken();

                req.setContinuationToken(token);

            } while (result.isTruncated());

        } catch (Exception ex) {

            throw new InternalServerErrorException("Unable to get policy names.", ex);

        }

        return names;

    }

    @Override
    public String get(String filterProfileName) {

        try {

            final S3Object fullObject = s3Client.getObject(new GetObjectRequest(bucket, buildKey(filterProfileName)));
            final String json = IOUtils.toString(fullObject.getObjectContent(), StandardCharsets.UTF_8);

            fullObject.close();

            return json;

        } catch (Exception ex) {

            throw new InternalServerErrorException("Unable to get policies.", ex);

        }

    }

    @Override
    public Map<String, String> getAll() {

        final Map<String, String> filterProfiles = new HashMap<>();

        try {

            ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucket).withPrefix(prefix);
            ListObjectsV2Result result;

            do {

                result = s3Client.listObjectsV2(req);

                for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {

                    final S3Object fullObject = s3Client.getObject(new GetObjectRequest(bucket, objectSummary.getKey()));
                    final String json = IOUtils.toString(fullObject.getObjectContent(), StandardCharsets.UTF_8);

                    fullObject.close();

                    final JSONObject object = new JSONObject(json);
                    final String name = object.getString("name");

                    filterProfiles.put(name, json);
                    LOGGER.debug("Added policy named {}", name);

                }

                // If there are more than maxKeys keys in the bucket, get a continuation token and list the next objects.
                final String token = result.getNextContinuationToken();

                req.setContinuationToken(token);

            } while (result.isTruncated());

        } catch (Exception ex) {

            throw new InternalServerErrorException("Unable to get policies.", ex);

        }

        return filterProfiles;

    }

    @Override
    public void save(String policyJson) {

        try {

            final JSONObject object = new JSONObject(policyJson);
            final String name = object.getString("name");

            final String key = buildKey(name);
            LOGGER.info("Uploading object to s3://{}/{}", bucket, key);
            s3Client.putObject(bucket, key, policyJson);

        } catch (JSONException ex) {

            LOGGER.error("The provided policy is not valid.", ex);
            throw new BadRequestException("The provided policy is not valid.");

        } catch (Exception ex) {

            throw new InternalServerErrorException("Unable to save policy.", ex);

        }

    }

    @Override
    public void delete(String name) {

        try {

            s3Client.deleteObject(bucket, buildKey(name));

        } catch (Exception ex) {

            throw new InternalServerErrorException("Unable to delete policy.", ex);

        }

    }

    private String buildKey(final String name) {

        LOGGER.info("Building key from: {} and {}", bucket, name);

        if(StringUtils.equals(prefix, "/")) {
            return name + ".json";
        } else if(prefix.endsWith("/")) {
            return "/" + name + ".json";
        } else {
            return prefix + "/" + name + ".json";
        }

    }

}

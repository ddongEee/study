package com.study.apps.poc.scc.client;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.DecryptResult;
import org.springframework.stereotype.Service;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

@Service
public class DecryptService {
    final AWSKMS kmsClient = AWSKMSClientBuilder.defaultClient();
    final DecryptRequest decryptRequest = new DecryptRequest().withKeyId("arn:aws:kms:ap-northeast-2:{{ACCOUNT_ID}}:key/{{KEY_SECRETS}}");

    public String decrypt(String key, String ciphertext) {
        ByteBuffer ciphertextBlob = ByteBuffer.wrap(Base64.getDecoder().decode(ciphertext));
        Map<String, String> encryptionContext = Collections.singletonMap("PARAMETER_ARN", "arn:aws:ssm:ap-northeast-2:{{ACCOUNT_ID}}:parameter/config/somedomain-dev/" + key);
        DecryptRequest decryptRequest = this.decryptRequest.withEncryptionContext(encryptionContext)
                                                           .withCiphertextBlob(ciphertextBlob);
        DecryptResult decryptResult = kmsClient.decrypt(decryptRequest);
        ByteBuffer plainText = decryptResult.getPlaintext();
        return new String(plainText.array(), StandardCharsets.UTF_8);
    }
}

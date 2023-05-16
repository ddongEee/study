```bash
# local build 파일을 s3에 sync
export DEST_BUCKET=$(cat ~/.tf/poc/output.json | jq -r .aws_s3_react_bucket.value) && \
source ~/.tf/poc/loadInput2Env.sh && \
aws s3 sync ${UI_PROJECT_DIR}/build s3://${DEST_BUCKET}/
# cloudfront invalidation 

# 아직 아래 커맨드 테스트 안해봄..
export CF_DIST_ID=export DEST_BUCKET=$(cat ~/.tf/poc/output.json | jq -r aws_cloudfront_distribution_id.value) && \
$ aws cloudfront create-invalidation --distribution-id ${CF_DIST_ID} --paths "[파일 절대경로명1]" "[파일 절대경로명2]"
```


import { Logger, injectLambdaContext } from '@aws-lambda-powertools/logger';
import middy from '@middy/core';

// https://medium.com/@uday.rayala/how-to-create-your-own-aws-lambda-middleware-framework-in-node-js-e65f23bc0ac
// https://aws.amazon.com/ko/blogs/compute/simplifying-serverless-best-practices-with-aws-lambda-powertools-for-typescript/
// https://blog.logrocket.com/writing-aws-lambda-middleware-middy-js/
// https://awslabs.github.io/aws-lambda-powertools-typescript/0.10.0/core/logger/#capturing-lambda-context-info
// https://blog.codeminer42.com/aws-sam-and-middy-with-lambda-layers/
// https://aws.amazon.com/ko/blogs/opensource/simplifying-serverless-best-practices-with-lambda-powertools/

/**
 * # console.log 사용시
 * - console methods do not support log levels : print to the standard output or standard error without indicating log severity.
 * 요구사항
 * - log format as json..
 * -
 *
 * # middy 사용
 * - Middy.js is specifically tailored for AWS Lambda and serverless applications,
 * - easy middleware control 가능.
 * - adding log context before excute mainHandler
 * - can integrate with "aws-lambda-powertools"
 */
export const log = new Logger({
    logLevel: 'INFO',
    serviceName: 'something-api'
});

export const handle = (mainHandler) =>  middy(mainHandler)
    .use(injectLambdaContext(log))
    .before(setTenantContextOnLogger);

const setTenantContextOnLogger = async (request) => {
    const tenantId = request.event.headers.tenantId;
    const tenantCategory = request.event.headers.tenantCategory;
    log.addPersistentLogAttributes({
        tenantId: tenantId,
        tenantGroup: {
            id: tenantId,
            category: tenantCategory
        }
    });
}

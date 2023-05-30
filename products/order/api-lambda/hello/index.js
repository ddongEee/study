import {handle, log} from "./proserve.module.js";

/**
 * AS-IS
 * export const handler = async (event, context) => {
 *   mainFunction 로직 관련,,,
 * }
 */

const mainFunction = async () => {
    // 요기에 메인로직
    log.info('This is an INFO log with some context : v15');
    // 요기도 메인로직
};

export const handler = (event, context) => {
    return handle(mainFunction)(event, context);
}

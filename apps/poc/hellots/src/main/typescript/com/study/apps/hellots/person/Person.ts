import * as U from '@src/utils/makeRandomNumber'
import IPerson from "@src/person/IPerson";


export default class Person implements IPerson {
    constructor(public name: string, public age: number) {}
}

export const makePerson = (name: string, age:number = U.makeRandomNumber()) => ({name,age})

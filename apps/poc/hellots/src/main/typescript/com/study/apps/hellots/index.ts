import IPerson from '@src/person/IPerson'
import Person, {makePerson} from '@src/person/Person'

const testMakePerson = (): void => {
    let jane: IPerson = makePerson('Jane')
    let jack: IPerson = new Person('Jack',43)
    console.log(jane, jack)
}

testMakePerson()

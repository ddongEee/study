package com.study.sandbox.annotation

import com.fasterxml.jackson.databind.ObjectMapper
import com.study.libs.jackson.annotation.AliasBean
import com.study.libs.jackson.annotation.CustomSerializer
import com.study.libs.jackson.annotation.SerializeObject
import spock.lang.Specification

import java.text.SimpleDateFormat

class SerializeObjectTest extends Specification {
    def mapper = new ObjectMapper()

    def "JsonAndGetter Test"() {
        given:
        def bean = new SerializeObject.ExtendableBean()
        bean.name = "testBeanName"
        bean.properties = new HashMap<>()
        bean.properties.put("first", "hello")
        bean.properties.put("second", "world")
        expect:
        def serializedBean = mapper.writeValueAsString(bean)
        serializedBean.contains("\"first\":\"hello\"")
    }

    def "JsonValue test"() {
        expect:
        var stringEnum = mapper.writeValueAsString(SerializeObject.TypeEnumWithValue.TYPE1)
        stringEnum == "\"TypeA\""
    }

    def "JsonSerialize test"() {
        expect:
        var df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        var toParse = "2014-12-20 02:30:00"
        Date date = df.parse(toParse)
        CustomSerializer.EventWithSerializer event = new CustomSerializer.EventWithSerializer("party", date)

        var result = new ObjectMapper().writeValueAsString(event)
        result.contains(toParse)
        println result
    }

    def "AliasBean test"() {
        expect:
        var json = "{\"fName\": \"John\", \"lastName\": \"Green\"}";
//        AliasBean aliasBean = new ObjectMapper().readerFor(AliasBean.class).readValue(json);
        AliasBean aliasBean = mapper.readValue(json, AliasBean.class)
        "John" == aliasBean.getFirstName()
    }
}

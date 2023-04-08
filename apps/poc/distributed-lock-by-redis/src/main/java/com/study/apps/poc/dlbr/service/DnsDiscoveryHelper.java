package com.study.apps.poc.dlbr.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.xbill.DNS.*;
import org.xbill.DNS.Record;

@Slf4j
@Component
public class DnsDiscoveryHelper {
    public String nsLookupCNAME(String domain) {
        try {
            Record[] rs = new Lookup(domain, Type.CNAME).run();
//            return ((CNAMERecord) rs[0]).getName().toString();
            return ((CNAMERecord) rs[0]).getTarget().toString();
        } catch (TextParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void nsLookupCNAMEAll(String domain) {
        try {
            Record[] rs = new Lookup(domain, Type.CNAME).run();
            for (Record record : rs) {
//            return ((CNAMERecord) rs[0]).getName().toString();
                log.info(record.toString());
//                log.info(((CNAMERecord) record).getName().toString());
                log.info(((CNAMERecord) record).getTarget().toString());
//                log.info(record.getAdditionalName().toString());
            }
        } catch (TextParseException e) {
            e.printStackTrace();
        }
    }
}

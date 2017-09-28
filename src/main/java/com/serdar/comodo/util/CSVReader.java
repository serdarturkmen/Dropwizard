package com.serdar.comodo.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.lang.StringUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;


/**
 * Singleton class that adds protocol numbers to hashmap on initializing app
 * from /protocol-numbers.csv file
 */
public class CSVReader {

    private static final BiMap<Integer, String> protocolStorage =  HashBiMap.create();
    private static CSVReader instance = null;

    private CSVReader() {
    }

    public void init() {

        String csvFile = "/protocol-numbers.csv";
        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(csvFile)))) {

            while ((line = br.readLine()) != null) {

                // get Numeric rows from cvs file and add it to the storage
                String[] protocols = line.split(cvsSplitBy);
                if(StringUtils.isNumericSpace(protocols[0]) && !protocolStorage.containsValue(protocols[1])){

                    protocolStorage.put(Integer.parseInt(protocols[0]), protocols[1]);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String convertNumberToCode(int number) {
        return protocolStorage.get(number);
    }

    public int convertCodeToNUmber(String code) {
        return protocolStorage.inverse().get(code);
    }

    public static CSVReader getInstance() {
        if(instance == null) {
            instance = new CSVReader();
        }
        return instance;
    }

}
package com.serdar.comodo.util;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.net.util.SubnetUtils;

/**
 * this class is responsible for converting both ip address to decimal and decimai to ip address
 * for example 192.168.1.2 is equivalent to 92 x (256)^3 + 168 x (256)^2 + 1 x (256)^1 + 2 (256)^0 = 3232235778
 */
public class IpAddressConverter {

    /**
     * coverting base256 ip address to decimal(base 10)
     * @param ipAddress
     * @return
     */
    public static long ipToLong(String ipAddress) {

        String[] ipAddressInArray = ipAddress.split("\\.");

        long result = 0;
        for (int i = 0; i < ipAddressInArray.length; i++) {

            int power = 3 - i;
            int ip = Integer.parseInt(ipAddressInArray[i]);
            result += ip * Math.pow(256, power);

        }

        return result;
    }

    /**
     * coverting decimal ip address to base256 form
     * @param ip
     * @return
     */
    public static String longToIp(long ip) {
        StringBuilder result = new StringBuilder(15);

        for (int i = 0; i < 4; i++) {

            result.insert(0,Long.toString(ip & 0xff));

            if (i < 3) {
                result.insert(0,'.');
            }

            ip = ip >> 8;
        }
        return result.toString();
    }

    /**
     *
     * @param cidrNotation
     * @return ip address range between ipblock
     * @throws IllegalArgumentException
     */
    public static Pair<String, String> getIpBlockFromCidrMask(String cidrNotation) throws IllegalArgumentException{
        SubnetUtils utils = new SubnetUtils(cidrNotation);
        return Pair.of(utils.getInfo().getLowAddress(), utils.getInfo().getHighAddress());
    }
}

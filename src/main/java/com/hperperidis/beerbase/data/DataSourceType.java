package com.hperperidis.beerbase.data;

/**
 * Basic Enumeration, to be used as a set of constant values to be allowed
 * to the {@code Beer} data source fields.
 * Nothing special here.
 *
 * @author C. Perperidis(ta6hbe@hotmail.com)
 *
 */
public enum DataSourceType {
    REST_API("rest_api"),
    LOCAL_FILE("local_file"),
    PUNK_API("punk_api");

    public final String label;

    DataSourceType(String label) {
        this.label = label;
    }

    public static DataSourceType valueOfLabel(String label) {
        for (DataSourceType dtst : values()) {
            if (dtst.label.equals(label)) {
                return dtst;
            }
        }
        return null;
    }
}

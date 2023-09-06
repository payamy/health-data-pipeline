package com.payamy.etl.job.cache;

import com.payamy.etl.cache.Cache;
import com.payamy.etl.entity.User;
import com.payamy.etl.types.BloodType;
import com.payamy.etl.types.EyeColor;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.spark.api.java.function.MapFunction;

public class UserCache extends Cache<User> {

    public UserCache( String[] bootstrapServers, String sourceTopic, String cacheName )
            throws ConfigurationException, ClassNotFoundException {
        super(bootstrapServers, sourceTopic, cacheName);
    }

    @Override
    protected void manipulateDataFrame() {

        ds = ds.map((MapFunction<User, User>) row ->
                {
                    row.setBlood_type(
                            String.valueOf(BloodType.values()[Integer.parseInt(row.getBlood_type())])
                    );
                    row.setEye_color(
                            String.valueOf(EyeColor.values()[Integer.parseInt(row.getEye_color())])
                    );
                    return row;
                },
                this.encoder
        );
    }
}

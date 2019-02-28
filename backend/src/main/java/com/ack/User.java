 package com.ack;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users_trackbook_ac")

/*e
 * Created by Lucky Goyal on 12/10/2016
 */
public class User {
        @DatabaseField(id=true, columnName = "phone")
        private String phone;


        @DatabaseField(index = true)
        private String name;

        @DatabaseField(index = true)
        private String gcm_id;

        public User()
        {
        }

        public User(final String name, final String phone, final String gcm_id)
        {
            this.name = name;
            this.gcm_id = gcm_id;
            this.phone = phone;
        }
    public String getGcm_id()
    {
        return gcm_id;
    }

    public void setGcm_id(String gcm_id)
    {
        this.gcm_id = gcm_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPhone()
    {
        return phone;
    }
    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    @Override
    public String toString()
    {
        return name + " : " + gcm_id;
    }
}

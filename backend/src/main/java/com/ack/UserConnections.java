 package com.ack;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

 @DatabaseTable(tableName = "users_trackbook_connections")

 /*e
  * Created by Lucky Goyal on 12/10/2016
  */
 public class UserConnections {

        @DatabaseField(generatedId = true)
        private int id;

         @DatabaseField(index = true)
         private String m_phone;


         @DatabaseField(index = true)
         private String m_name;

         @DatabaseField(index = true)
         private String m_gcm_id;

         @DatabaseField(index= true)
         private String f_gcm_id;

         @DatabaseField(index = true)
         private String f_name;

         @DatabaseField(index = true)
         private String f_phone;

         @DatabaseField(index = true)
         private String conn_ind;

         public UserConnections()
         {
         }

         public UserConnections(final String m_name, final String m_phone, final String m_gcm_id,
                                final String f_name, final String f_phone, final String f_gcm_id,
                                final String conn_ind)
         {
             this.m_name = m_name;
             this.m_gcm_id = m_gcm_id;
             this.m_phone = m_phone;
             this.f_name = f_name;
             this.f_gcm_id = f_gcm_id;
             this.f_phone = f_phone;
             this.conn_ind = conn_ind;
         }
     public String get_m_Gcm_id()
     {
         return m_gcm_id;
     }

     public void set_m_Gcm_id(String m_gcm_id)
     {
         this.m_gcm_id = m_gcm_id;
     }

     public String get_m_Name() {
         return m_name;
     }

     public void set_m_Name(String m_name)
     {
         this.m_name = m_name;
     }

     public String get_m_Phone()
     {
         return m_phone;
     }
     public void set_m_Phone(String m_phone)
     {
         this.m_phone = m_phone;
     }

     public String get_f_Gcm_id()
     {
         return f_gcm_id;
     }

     public void set_f_Gcm_id(String f_gcm_id)
     {
         this.f_gcm_id = f_gcm_id;
     }

     public String get_f_Name() {
         return f_name;
     }

     public void set_f_Name(String f_name)
     {
         this.f_name = f_name;
     }

     public String get_f_Phone()
     {
         return f_phone;
     }
     public void set_f_Phone(String f_phone)
     {
         this.f_phone = f_phone;
     }
     public String get_conn_id()
     {
         return conn_ind;
     }
     public void set_conn_id(String Conn_id)
     {
         this.conn_ind = Conn_id;
     }

     @Override
     public String toString()
     {
         return m_name + " : " + m_gcm_id;
     }
 }

package com.apartmentV2.Utility;

import java.util.HashMap;

/**
This process synchronizer ensure that hat request of same time work one by one if . but the different process work fine
to use the first to call lock() method and pass the Id and table type and release lock to call release() method
*/
public class ProcessSyncronizer {

   private static class lockingObject {

       int count = 0;

       String Id;

       lockingObject(String Id) {

           this.Id = Id;

       }

   }

   private ProcessSyncronizer() {

   }

   private static HashMap<String, lockingObject> hash = new HashMap();

   public static Object lock(Long Id, String tableId) {

       String hashID = "" + Id + tableId;

       return lock(hashID);
   }

   public static Object lock(String hashID) {

       lockingObject object;

       synchronized (hash) {

           object = hash.get(hashID);

           if (object == null) {

               object = new lockingObject(hashID);

               hash.put(hashID, object);

           }
       }

       synchronized (object) {

           try {

               object.count++;

               if (object.count > 1) {

                   object.wait();

               }

           } catch (InterruptedException ex) {

               ex.printStackTrace();
           }
       }

       return object;
   }

   public static void release(Object token) {

       lockingObject object = (lockingObject) token;

       synchronized (object) {

           object.notify();

           object.count--;

           if (object.count <= 0) {

               synchronized (hash) {

                   hash.remove(object.Id);

               }
           }

       }

   }

}
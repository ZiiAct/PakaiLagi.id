package com.example.pakailagi.data;

public class DataRepository {
    private static DataRepository instance;

    private DataRepository() {
        // Initialize repository variables
    }

    public static synchronized DataRepository getInstance() {
        if (instance == null) {
            instance = new DataRepository();
        }
        return instance;
    }

    // Add future Firebase / local database functions here...
}

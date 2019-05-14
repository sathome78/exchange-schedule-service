package me.exrates.scheduleservice.repositories;

public interface ApiAuthTokenDao {

    int deleteAllExpired();
}
package me.elijuh.duels.storage.dao;

import lombok.RequiredArgsConstructor;
import me.elijuh.duels.objects.DuelsStatistic;
import me.elijuh.duels.objects.StatisticsProfile;
import me.elijuh.duels.storage.Dao;
import me.elijuh.duels.storage.storages.MySQLStorage;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@RequiredArgsConstructor
public class MySQLDao implements Dao {
    private final MySQLStorage storage;

    @Override
    public void save(StatisticsProfile entry) {
        try (Connection connection = storage.connection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO `userdata`" +
                     "(`uuid`, `name`, `wins`, `losses`, `kills`, `deaths`, `streak`) " +
                     "VALUES(?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                     "`name`=?, `wins`=?, `losses`=?, `kills`=?, `deaths`=?, `streak`=?;")) {
            ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
            buffer.putLong(entry.getUuid().getMostSignificantBits());
            buffer.putLong(entry.getUuid().getLeastSignificantBits());
            statement.setBytes(1, buffer.array());
            statement.setString(2, entry.getName());
            statement.setString(8, entry.getName());
            for (int i = 0; i < 5; i++) {
                int stat = entry.get(DuelsStatistic.values()[i]);
                statement.setInt(3 + i, stat);
                statement.setInt(9 + i, stat);
            }
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public StatisticsProfile get(UUID uuid) {
        try (Connection connection = storage.connection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `userdata` WHERE `uuid`=?;")) {
            ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
            buffer.putLong(uuid.getMostSignificantBits());
            buffer.putLong(uuid.getLeastSignificantBits());
            statement.setBytes(1, buffer.array());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                StatisticsProfile profile = new StatisticsProfile(uuid, result.getString("name"));
                for (DuelsStatistic statistic : DuelsStatistic.values()) {
                    profile.put(statistic, result.getInt(statistic.getKey()));
                }
                return profile;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public StatisticsProfile getByName(String name) {
        try (Connection connection = storage.connection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM `userdata` WHERE LOWER(`name`)=?;")) {
            statement.setString(1, name.toLowerCase());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                ByteBuffer buffer = ByteBuffer.wrap(result.getBytes("uuid"));
                UUID uuid = new UUID(buffer.getLong(), buffer.getLong());

                StatisticsProfile profile = new StatisticsProfile(uuid, result.getString("name"));
                for (DuelsStatistic statistic : DuelsStatistic.values()) {
                    profile.put(statistic, result.getInt(statistic.getKey()));
                }
                return profile;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

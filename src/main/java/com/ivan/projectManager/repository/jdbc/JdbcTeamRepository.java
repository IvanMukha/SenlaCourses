package com.ivan.projectManager.repository.jdbc;

import com.ivan.projectManager.model.Team;
import com.ivan.projectManager.repository.TeamRepository;
import com.ivan.projectManager.repository.repositoryMapper.RowMapper;
import com.ivan.projectManager.repository.repositoryMapper.TeamRowMapper;
import com.ivan.projectManager.utils.ConnectionHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class JdbcTeamRepository implements TeamRepository {

    private final ConnectionHolder connectionHolder;
    private final RowMapper<Team> rowMapper;

    @Autowired
    public JdbcTeamRepository(ConnectionHolder connectionHolder, RowMapper<Team> rowMapper) {
        this.connectionHolder = connectionHolder;
        this.rowMapper=rowMapper;
    }

    @Override
    public List<Team> getAll() {
        List<Team> teams = new ArrayList<>();
        try (Connection connection = connectionHolder.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM teams");
             ResultSet resultSet = statement.executeQuery()) {
            teams = rowMapper.mapAll(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all teams", e);
        }
        connectionHolder.releaseConnection();
        return teams;
    }

    @Override
    public Team save(Team team) {
        String sql = "INSERT INTO teams (id, name) VALUES (?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionHolder.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, team.getId());
            statement.setString(2, team.getName());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating team failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save team", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to close statement", e);
                }
            }
        }
        return team;
    }


    @Override
    public Optional<Team> getById(int id) {
        String sql = "SELECT * FROM teams WHERE id = ?";
        try (Connection connection = connectionHolder.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return Optional.ofNullable(rowMapper.map(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get team by id", e);
        } finally {
            connectionHolder.releaseConnection();
        }
    }

    @Override
    public Optional<Team> update(int id, Team updatedTeam) {
        String sql = "UPDATE teams SET name = ? WHERE id = ?";
        try (Connection connection = connectionHolder.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, updatedTeam.getName());
            statement.setInt(2, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return Optional.empty();
            }
            return Optional.of(updatedTeam);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update team", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM teams WHERE id = ?";
        try (Connection connection = connectionHolder.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete team", e);
        }
    }
}

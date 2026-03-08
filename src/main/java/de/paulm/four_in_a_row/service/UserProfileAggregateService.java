package de.paulm.four_in_a_row.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import de.paulm.four_in_a_row.domain.exceptions.PlayerProfileNotFoundException;
import de.paulm.four_in_a_row.domain.exceptions.UserNotFoundException;
import de.paulm.four_in_a_row.domain.player.PlayerProfile;
import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.four_in_a_row.domain.security.UserProfileAggregate;
import de.paulm.four_in_a_row.repository.PlayerProfileRepository;
import de.paulm.four_in_a_row.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileAggregateService {

    private final UserRepository userRepository;
    private final PlayerProfileRepository playerRepository;

    @NonNull
    public UserProfileAggregate getAggregateByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId darf nicht null sein");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        PlayerProfile player = playerRepository.findByUserId(userId)
                .orElseThrow(() -> new PlayerProfileNotFoundException(userId, "userId"));

        return new UserProfileAggregate(user, player);
    }

    public List<UserProfileAggregate> getAllAggregates() {
        List<User> users = userRepository.findAll();
        List<Long> userIds = users.stream().map(user -> user.getId()).toList();
        List<PlayerProfile> profiles = getAllProfilesByUserIds(userIds);

        return combineUsersAndProfiles(users, profiles);
    }

    public List<UserProfileAggregate> getAggregatesByUserIds(List<Long> userIds) {
        if (userIds == null) {
            throw new IllegalArgumentException("userIds darf nicht null sein");
        } else if (userIds.isEmpty()) {
            return List.of();
        }

        List<User> users = userRepository.findAllById(userIds);
        List<PlayerProfile> profiles = getAllProfilesByUserIds(userIds);

        return combineUsersAndProfiles(users, profiles);
    }

    private List<PlayerProfile> getAllProfilesByUserIds(List<Long> userIds) {
        if (userIds == null) {
            throw new IllegalArgumentException("userIds darf nicht null sein");
        } else if (userIds.isEmpty()) {
            return List.of();
        }
        return playerRepository.findAllByUserIdIn(userIds);
    }

    private List<UserProfileAggregate> combineUsersAndProfiles(List<User> users, List<PlayerProfile> profiles) {
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return profiles.stream()
                .map(profile -> new UserProfileAggregate(
                        userMap.get(profile.getUserId()),
                        profile))
                .filter(agg -> agg.user() != null) // Falls ein User gelöscht wurde
                .toList();
    }
}

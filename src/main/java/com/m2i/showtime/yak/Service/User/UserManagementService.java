package com.m2i.showtime.yak.Service.User;

import com.m2i.showtime.yak.Dto.AddUserAGgridDto;
import com.m2i.showtime.yak.Dto.NotificationAgGridDto;
import com.m2i.showtime.yak.Dto.ResponseApiAgGridDto;
import com.m2i.showtime.yak.Dto.Search.PageListResultDto;
import com.m2i.showtime.yak.Dto.Search.SearchParamsDto;
import com.m2i.showtime.yak.Dto.UpdateUserDto;
import com.m2i.showtime.yak.Entity.Notification;
import com.m2i.showtime.yak.Entity.Role;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Repository.NotificationRepository;
import com.m2i.showtime.yak.Repository.RoleRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import com.m2i.showtime.yak.common.notification.NotificationStatus;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class UserManagementService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final NotificationRepository notificationRepository;

    private final RoleRepository roleRepository;
    @Autowired
    public UserManagementService(UserRepository userRepository, PasswordEncoder passwordEncoder, NotificationRepository notificationRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationRepository = notificationRepository;
        this.roleRepository = roleRepository;
    }

    public PageListResultDto getAllUsers(SearchParamsDto searchParamsDto){
        Page<User> page;
        Sort.Direction sortDirection;

        if (searchParamsDto.getSort()
                .getSortField() == null) {

            page = userRepository.findAll(
                    PageRequest.of(searchParamsDto.getPageNumber(), searchParamsDto.getLimitRow(),
                            Sort.by(Sort.Direction.ASC, "username")));
        } else {
            if (searchParamsDto.getSort()
                    .getSortOrder() == 1) {
                sortDirection = Sort.Direction.ASC;
            } else {
                sortDirection = Sort.Direction.DESC;
            }
            page = userRepository.findAll(
                    PageRequest.of(searchParamsDto.getPageNumber(), searchParamsDto.getLimitRow(),
                            Sort.by(sortDirection, searchParamsDto.getSort()
                                    .getSortField())));
        }

        if (page.isEmpty()) {
            throw new IllegalStateException("page was not found");
        }

        return new PageListResultDto(page.toList(), page.getTotalElements());
    }

    public void registerNewUser(User user) {

    }
    public ResponseApiAgGridDto registerNewUserAgGrid(AddUserAGgridDto user) {
        ResponseApiAgGridDto response = new ResponseApiAgGridDto();
        Optional<User> userOptional = userRepository.findUserByEmail(user.getUsername());
        if(userOptional.isPresent()){
            response.setSeverity("error");
            response.setTitle("Error");
            response.setDetails("Email is already taken");
            response.setSticky(false);
            return response;
        }
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setCountry(user.getCountry());
        newUser.setRole(user.getRole());
        newUser.setPassword(user.getPassword());
        newUser.setGrantedAuthorities(newUser.getGrantedAuthorities());
        userRepository.save(newUser);
        response.setSeverity("success");
        response.setTitle("Success");
        response.setDetails("User added successfully");
        response.setSticky(false);
        return response;

    }
    public List<User> getAllUsersAggrid(){
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            user.setPassword("");
            user.setComments(null);
            user.setFollowing(null);
            user.setFollowers(null);
        }
        return allUsers;
    }
    public void deleteUser(Long userId){
        if (!userRepository.existsById(userId)){
            throw new IllegalStateException("User does not exists");
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    public void updateUser(Long userId, User modifiedUser) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException(("user with id "+ userId + "does not exists")));

        if (modifiedUser.getFirstName() != null &&
                modifiedUser.getFirstName().length() > 0 &&
                !Objects.equals(user.getFirstName(), modifiedUser.getFirstName())) {
            user.setFirstName(modifiedUser.getFirstName());
        }

        if (modifiedUser.getLastName() != null &&
                modifiedUser.getLastName().length() > 0 &&
                !Objects.equals(user.getLastName(), modifiedUser.getLastName())) {
            user.setLastName(modifiedUser.getLastName());
        }

        if (modifiedUser.getCountry() != null &&
                modifiedUser.getCountry().length() > 0 &&
                !Objects.equals(user.getCountry(), modifiedUser.getCountry())) {
            user.setCountry(modifiedUser.getCountry());
        }

        if (modifiedUser.getUsername() != null &&
                modifiedUser.getUsername().length() > 0 &&
                !Objects.equals(user.getUsername(), modifiedUser.getUsername())) {
            if (userRepository.findUserByEmail(modifiedUser.getUsername()).isPresent()){
                throw new IllegalStateException("email taken");
            }
            user.setUsername(modifiedUser.getUsername());
        }
    }

    public ResponseApiAgGridDto editUserAggrid(UpdateUserDto userToModify){
        ResponseApiAgGridDto response = new ResponseApiAgGridDto();
        try{
            User user = userRepository.findById(userToModify.getId())
                    .orElseThrow(() -> new IllegalStateException(("user with id "+ userToModify.getId() + "does not exists")));
            new ModelMapper().map(userToModify,user);
            userRepository.save(user);

            response.setSeverity("success");
            response.setTitle("Success");
            response.setDetails("User modified successfully");
            response.setSticky(false);
            return response;
        }catch (Exception e){
            response.setSeverity("error");
            response.setTitle("Error");
            response.setDetails("An error occurred while modifying the user");
            response.setSticky(false);
            return response;
        }

    }

    public Set<Notification> getUserNotifications(String username) {
        User user = userRepository.findUserByEmail(username)
                .orElseThrow(() -> new IllegalStateException("user with username " + username + " does not exists"));
        return user.getNotifications();
    }

    public void updateUserAlertNotifications(String username) {
        User user = userRepository.findUserByEmail(username)
                .orElseThrow(() -> new IllegalStateException("user with username " + username + " does not exists"));
         user.getNotifications()
                .forEach(notification -> {
                    String currentType = notification.getType();
                    NotificationStatus currentStatus = notification.getStatus();
                    boolean check = currentType.equals("alert") && currentStatus.equals(NotificationStatus.UNREAD);
                        if(check)
                        {
                            notification.setDateRead(new Date());
                            notification.setStatus(NotificationStatus.READ);
                            this.notificationRepository.save(notification);
                        }
                    }
                );
        userRepository.save(user);
    }

    public List<Role> getAllRoles() {
        List<Role>  roles = this.roleRepository.findAll();
        return roles;
    }

    public List<Map<String, Object>>  getAllNotifications() {
        List<Map<String, Object>>  notifications = this.notificationRepository.findAllNotificationAndAssociatedReciever();
        return notifications;
    }

    public ResponseApiAgGridDto updateNotification(Notification notificationToUpdate) {
        ResponseApiAgGridDto response = new ResponseApiAgGridDto();
        try{
            Notification notification = notificationRepository.findById(notificationToUpdate.getId())
                    .orElseThrow(() -> new IllegalStateException(("user with id "+ notificationToUpdate.getId() + "does not exists")));
            new ModelMapper().map(notificationToUpdate,notification);
            notificationRepository.save(notification);

            response.setSeverity("success");
            response.setTitle("Success");
            response.setDetails("Notification modified successfully");
            response.setSticky(false);
            return response;
        }catch (Exception e){
            response.setSeverity("error");
            response.setTitle("Error");
            response.setDetails("An error occurred while modifying the notification");
            response.setSticky(false);
            return response;
        }
    }

    public ResponseApiAgGridDto deleteNotification(NotificationAgGridDto notificationToDelete) {
        ResponseApiAgGridDto response = new ResponseApiAgGridDto();
        try{
            Notification notification = notificationRepository.findById(notificationToDelete.getId())
                    .orElseThrow(() -> new IllegalStateException(("user with id "+ notificationToDelete.getId() + "does not exists")));
            Optional<User> userToRemoveNotificationFrom =  userRepository.findUserByEmail(notificationToDelete.getReceiverName());
            if(userToRemoveNotificationFrom.isPresent()){
                User user = userToRemoveNotificationFrom.get();
                user.getNotifications().remove(notification);
                userRepository.save(user);
            }
            notificationRepository.delete(notification);

            response.setSeverity("success");
            response.setTitle("Success");
            response.setDetails("Notification deleted successfully");
            response.setSticky(false);
            return response;
        }catch (Exception e){
            response.setSeverity("error");
            response.setTitle("Error");
            response.setDetails("An error occurred while deleting the notification");
            response.setSticky(false);
            return response;
        }
    }
}

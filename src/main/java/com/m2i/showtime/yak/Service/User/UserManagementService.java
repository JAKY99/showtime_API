package com.m2i.showtime.yak.Service.User;

import com.m2i.showtime.yak.Dto.AddUserAGgridDto;
import com.m2i.showtime.yak.Dto.ResponseApiAgGridDto;
import com.m2i.showtime.yak.Dto.Search.PageListResultDto;
import com.m2i.showtime.yak.Dto.Search.SearchParamsDto;
import com.m2i.showtime.yak.Dto.UpdateUserDto;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Repository.UserRepository;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserManagementService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserManagementService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
    public Object[] getAllUsersAggrid(){
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).toArray();
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
}

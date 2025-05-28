package com.trustreview.trustreview.Model;

import com.trustreview.trustreview.Entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class UserReponse extends Users {
    String token;
}
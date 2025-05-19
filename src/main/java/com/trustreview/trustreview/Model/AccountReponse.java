package com.trustreview.trustreview.Model;

import com.trustreview.trustreview.Entity.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class AccountReponse extends Account {
    String token;
}
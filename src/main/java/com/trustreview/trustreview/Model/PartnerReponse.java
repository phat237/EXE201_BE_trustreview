package com.trustreview.trustreview.Model;

import com.trustreview.trustreview.Entity.Partner;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class PartnerReponse extends Partner {
    String token;
}
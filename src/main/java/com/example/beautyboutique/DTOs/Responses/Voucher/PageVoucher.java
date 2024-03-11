package com.example.beautyboutique.DTOs.Responses.Voucher;

import com.example.beautyboutique.Models.BlogPost;
import com.example.beautyboutique.Models.Voucher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PageVoucher {
    private String message;
    private Integer quantity;
    private List<Voucher> voucherList;
}

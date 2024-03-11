package com.example.beautyboutique.DTOs.Responses.Voucher;

import java.math.BigDecimal;
import java.util.Date;

public class VoucherInput {
    private String title;
    private Float discount;
    private String content;
    private Integer quantity;
    private Integer numUsedVoucher;
    private BigDecimal maximDiscount;
    private BigDecimal minimumOrder;
    private Date startDate;
    private Date endDate;

}

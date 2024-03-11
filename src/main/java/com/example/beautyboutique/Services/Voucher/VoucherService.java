package com.example.beautyboutique.Services.Voucher;

import com.example.beautyboutique.Models.Comment;
import com.example.beautyboutique.Models.Voucher;
import com.example.beautyboutique.Models.VoucherDetail;

import java.util.List;

public interface VoucherService {
    public List<Voucher> getAllVoucher();

    List<VoucherDetail> getListVouchersByUserId(Integer userId);
    public boolean getVoucherDetailByUserIdAndVoucherId(Integer userId , Integer voucherId);
    public Voucher getAVoucherId(Integer id);
    public boolean deleteVoucher(Integer id );
    public Voucher createVoucher(Voucher voucher);
    public  VoucherDetail saveVoucherForUser(VoucherDetail voucherDetail);
    public Voucher updateVoucher(Integer id, Voucher updateVoucher);
}

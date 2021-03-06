package model.timeline;

import com.flexganttfx.model.activity.MutableActivityBase;
import model.ChiTietDatPhong;

public class ChiTietDatPhongWrapper extends MutableActivityBase<ChiTietDatPhong> {
    public ChiTietDatPhongWrapper(ChiTietDatPhong data) {
        setUserObject(data);
        setName(data.getDatPhong().getKhachHang().getTenKhach());
        setStartTime(data.getDatPhong().getNgayCheckinDk().toInstant());
        setEndTime(data.getDatPhong().getNgayCheckoutDk().toInstant());
    }
}
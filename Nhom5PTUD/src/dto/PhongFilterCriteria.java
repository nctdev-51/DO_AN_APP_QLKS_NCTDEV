package dto;

import java.util.Date;

/**
 * Lớp DTO (Data Transfer Object) chứa các tiêu chí
 * để lọc phòng từ TrangChu_Gui.
 */
public class PhongFilterCriteria {
    private Date checkInDate;
    private Date checkOutDate;
    private int priceMin;
    private int priceMax;
    private int totalGuests;
    private int maxRoomsWanted;

    // Constructor
    public PhongFilterCriteria(Date checkInDate, Date checkOutDate, int priceMin, int priceMax, int totalGuests, int maxRoomsWanted) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.priceMin = priceMin;
        this.priceMax = priceMax;
        this.totalGuests = totalGuests;
        this.maxRoomsWanted = maxRoomsWanted;
    }

    // Getters
    public Date getCheckInDate() { return checkInDate; }
    public Date getCheckOutDate() { return checkOutDate; }
    public int getPriceMin() { return priceMin; }
    public int getPriceMax() { return priceMax; }
    public int getTotalGuests() { return totalGuests; }
    public int getMaxRoomsWanted() { return maxRoomsWanted; }
}
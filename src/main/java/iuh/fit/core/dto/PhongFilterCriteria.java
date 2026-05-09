package iuh.fit.core.dto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO: PhongFilterCriteria
 * Mô tả: Tiêu chí lọc phòng từ GUI
 */
public class PhongFilterCriteria implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int totalGuests;
    private double priceMin;
    private double priceMax;
    private int maxRoomsWanted;

    public PhongFilterCriteria() {
    }

    public PhongFilterCriteria(LocalDate checkInDate, LocalDate checkOutDate, int totalGuests,
                              double priceMin, double priceMax, int maxRoomsWanted) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalGuests = totalGuests;
        this.priceMin = priceMin;
        this.priceMax = priceMax;
        this.maxRoomsWanted = maxRoomsWanted;
    }

    // Getters & Setters
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public int getTotalGuests() { return totalGuests; }
    public void setTotalGuests(int totalGuests) { this.totalGuests = totalGuests; }

    public double getPriceMin() { return priceMin; }
    public void setPriceMin(double priceMin) { this.priceMin = priceMin; }

    public double getPriceMax() { return priceMax; }
    public void setPriceMax(double priceMax) { this.priceMax = priceMax; }

    public int getMaxRoomsWanted() { return maxRoomsWanted; }
    public void setMaxRoomsWanted(int maxRoomsWanted) { this.maxRoomsWanted = maxRoomsWanted; }
}


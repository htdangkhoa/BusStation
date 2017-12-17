package vn.busmap.busstation.Models;

/**
 * Created by dangkhoa on 12/8/17.
 */

public class BusModel {
    String VehicleNumber, Time;
    Double Lat, Lng, Deg, Speed, Distance;

    int RouteId, Direction;
    String RouteNo, RouteName;

    public BusModel(String vehicleNumber, String time, Double lat, Double lng, Double deg, Double speed, Double distance, int routeId, int direction, String routeNo, String routeName) {
        VehicleNumber = vehicleNumber;
        Time = time;
        Lat = lat;
        Lng = lng;
        Deg = deg;
        Speed = speed;
        Distance = distance;
        RouteId = routeId;
        Direction = direction;
        RouteNo = routeNo;
        RouteName = routeName;
    }

    public String getVehicleNumber() {
        return VehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        VehicleNumber = vehicleNumber;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public Double getLat() {
        return Lat;
    }

    public void setLat(Double lat) {
        Lat = lat;
    }

    public Double getLng() {
        return Lng;
    }

    public void setLng(Double lng) {
        Lng = lng;
    }

    public Double getDeg() {
        return Deg;
    }

    public void setDeg(Double deg) {
        Deg = deg;
    }

    public Double getSpeed() {
        return Speed;
    }

    public void setSpeed(Double speed) {
        Speed = speed;
    }

    public Double getDistance() {
        return Distance;
    }

    public void setDistance(Double distance) {
        Distance = distance;
    }

    public int getRouteId() {
        return RouteId;
    }

    public void setRouteId(int routeId) {
        RouteId = routeId;
    }

    public int getDirection() {
        return Direction;
    }

    public void setDirection(int direction) {
        Direction = direction;
    }

    public String getRouteNo() {
        return RouteNo;
    }

    public void setRouteNo(String routeNo) {
        RouteNo = routeNo;
    }

    public String getRouteName() {
        return RouteName;
    }

    public void setRouteName(String routeName) {
        RouteName = routeName;
    }
}

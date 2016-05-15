package kpi.security.keyboard.handwriting.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * KeyboardHandwriting
 * Created 5/6/16, with IntelliJ IDEA
 *
 * @author Alex
 */

// User class for save istance in android when app is destroyed //Bundle.putParcelable()
public class Account implements Parcelable{
    String username;
    HashMap<String,Long> y;
    ArrayList<Double> s;
    long fullTime;
    int delCounter;

    public Account(String username, HashMap<String, Long> y, ArrayList<Double> s, long fullTime, int delCounter) {
        this.username = username;
        this.y = y;
        this.s = s;
        this.fullTime = fullTime;
        this.delCounter = delCounter;
    }

    public Account(Parcel in) {
        username=in.readString();
        y= (HashMap<String, Long>) in.readSerializable();
        s= (ArrayList<Double>) in.readSerializable();
        fullTime=in.readLong();
        delCounter=in.readInt();
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(username);
        out.writeSerializable(y);
        out.writeSerializable(s);
        out.writeLong(fullTime);
        out.writeInt(delCounter);
    }

    @Override
    public String toString() {
        return "Account{" +
                "username='" + username + '\'' +
                ", y=" + y.toString() +
                ", s=" + s.toString() +
                ", fullTime=" + fullTime +
                ", delCounter=" + delCounter +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public HashMap<String, Long> getY() {
        return y;
    }

    public void setY(HashMap<String, Long> y) {
        this.y = y;
    }

    public ArrayList<Double> getS() {
        return s;
    }

    public void setS(ArrayList<Double> s) {
        this.s = s;
    }

    public long getFullTime() {
        return fullTime;
    }

    public void setFullTime(long fullTime) {
        this.fullTime = fullTime;
    }

    public int getDelCounter() {
        return delCounter;
    }

    public void setDelCounter(int delCounter) {
        this.delCounter = delCounter;
    }

    public static Creator<Account> getCREATOR() {
        return CREATOR;
    }
}

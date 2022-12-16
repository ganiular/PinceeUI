package com.gnid.social.pincee.db.model;

import androidx.annotation.DrawableRes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UserProfile {
    public CharSequence story;
    public boolean autoWelcome;
    public CharSequence name;
    public int welcomeStatus;
    public ArrayList<CharSequence> numbers;
    public ArrayList<Integer> pictures;

    public UserProfile(CharSequence name, CharSequence story, boolean autoWelcome, int welcomeStatus, CharSequence[] numbers, @DrawableRes int[] pictures){
        this.name = name;
        this.story = story;
        this.autoWelcome = autoWelcome;
        this.welcomeStatus = welcomeStatus;
        this.numbers = new ArrayList<>(Arrays.asList(numbers));
        this.pictures = new ArrayList<>(pictures.length);
        for (int picture : pictures) {
            this.pictures.add(picture);
        }
    }
}

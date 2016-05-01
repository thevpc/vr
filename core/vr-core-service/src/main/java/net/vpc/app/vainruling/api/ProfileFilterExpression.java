/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api;

/**
 *
 * @author vpc
 */
public class ProfileFilterExpression {

    private String mainExpression;
    private String profileListExpression;
    private String filterExpression;

    public ProfileFilterExpression(String profileListExpression, String filterExpression) {
        this(
                (profileListExpression == null ? "" : profileListExpression)
                + " where "
                + (filterExpression == null ? "" : filterExpression),
                profileListExpression,
                filterExpression
        );
    }

    public ProfileFilterExpression(String mainExpression, String profileListExpression, String filterExpression) {
        if (mainExpression == null) {
            mainExpression = "";
        }
        if (profileListExpression == null) {
            profileListExpression = "";
        }
        if (filterExpression == null) {
            filterExpression = "";
        }
        this.mainExpression = mainExpression;
        this.profileListExpression = profileListExpression;
        this.filterExpression = filterExpression;
    }

    public ProfileFilterExpression(String mainExpression) {
        if (mainExpression == null) {
            mainExpression = "";
        }
        this.mainExpression = mainExpression;
        filterExpression = "";
        profileListExpression = mainExpression.trim();
        int fromIndex = 0;
        String profilePatternLower = profileListExpression.toLowerCase();
        int whereLength = "where".length();
        int profilePatternLowerLength = profilePatternLower.length();
        while (fromIndex < profilePatternLowerLength) {
            int i = profilePatternLower.indexOf("where", fromIndex);
            if (i < 0) {
                break;
            }
            if (i == 0) {
                String after = profileListExpression.substring(i + whereLength);
                if (after.trim().length() == 0) {
                    //just ignore where clause
                    profileListExpression = "";
                    break;
                } else if (!Character.isJavaIdentifierPart(after.charAt(0))) {
                    profileListExpression = "";
                    filterExpression = after.trim();
                    break;
                } else {
                    fromIndex = i + whereLength;
                    //just ignore it
                }
            } else {
                if (!Character.isJavaIdentifierPart(profileListExpression.charAt(i - 1))) {
                    String after = profileListExpression.substring(i + whereLength);
                    if (after.trim().length() == 0) {
                        //just ignore where clause
                        profileListExpression = profileListExpression.substring(0, i).trim();
                        break;
                    } else if (!Character.isJavaIdentifierPart(after.charAt(0))) {
                        profileListExpression = profileListExpression.substring(0, i).trim();
                        filterExpression = after.trim();
                        break;
                    } else {
                        fromIndex = i + whereLength;
                        //just ignore it
                    }
                } else {
                    fromIndex = i + whereLength;
                }
            }
        }
    }

    public String getMainExpression() {
        return mainExpression;
    }

    public String getProfileListExpression() {
        return profileListExpression;
    }

    public String getFilterExpression() {
        return filterExpression;
    }

    @Override
    public String toString() {
        return "ProfileFilterExpression{" + "mainExpression=\"" + mainExpression + "\", profileListExpression=\"" + profileListExpression + "\", filterExpression=\"" + filterExpression + "\"}";
    }

}

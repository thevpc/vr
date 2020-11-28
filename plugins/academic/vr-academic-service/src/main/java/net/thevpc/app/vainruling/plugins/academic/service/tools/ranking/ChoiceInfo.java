/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.tools.ranking;

import java.util.Arrays;

/**
 *
 * @author vpc
 */
public class ChoiceInfo {

    int number;
    int count;
    int available;
    int[] demands;

    @Override
    public String toString() {
        return "ChoiceInfo{" + "number=" + number + ", count=" + count + ", available=" + available + ", demands=" + (demands == null ? "" : Arrays.toString(demands)) + '}';
    }
}

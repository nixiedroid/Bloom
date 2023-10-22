package com.nixiedroid.bloomlwp.wallpapers.timelapse;

import com.nixiedroid.bloomlwp.util.MathUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GradientSetManager {
    public List<GradientSet> gradientSets;
    private final Gradient scratch1 = new Gradient();
    private final Gradient scratch2 = new Gradient();

    public GradientSetManager() {
        GradientSet gradientSet = new GradientSet(new Gradient("#F8C66F", "#CE93D8", "#0F284C", "#455A64", "#212121"), new Gradient("#CE93D8", "#F48FB1", "#2B1780", "#0D47A1", "#0A3880"), new Gradient("#8EF6D6", "#FFF59D", "#1A1885", "#9575CD", "#006BC3"), new Gradient("#4fadf6", "#93c7fa", "#303f9f", "#52b6b3", "#1a3da3"), new Gradient("#FFAB91", "#CE93D8", "#311B92", "#1976D2", "#1A237E"), new Gradient("#DC5A7A", "#9739B2", "#311B92", "#6A189A", "#311B92"));
        GradientSet gradientSet2 = new GradientSet(new Gradient("#F8C66F", "#CE93D8", "#0F284C", "#455A64", "#212121"), new Gradient("#CE93D8", "#F48FB1", "#2B1780", "#0D47A1", "#0A3880"), new Gradient("#8EF6D6", "#FFF59D", "#1A1885", "#9575CD", "#006BC3"), new Gradient("#64B5F6", "#81D4FA", "#303F9F", "#279CAC", "#1A237E"), new Gradient("#FFAB91", "#CE93D8", "#311B92", "#1976D2", "#1A237E"), new Gradient("#DC5A7A", "#9739B2", "#311B92", "#6A189A", "#311B92"));
        GradientSet gradientSet3 = new GradientSet(new Gradient("#F8C66F", "#CE93D8", "#0F284C", "#455A64", "#212121"), new Gradient("#CE93D8", "#F48FB1", "#2B1780", "#0D47A1", "#0A3880"), new Gradient("#8EF6D6", "#FFF59D", "#1A1885", "#9575CD", "#006BC3"), new Gradient("#64B5F6", "#81D4FA", "#303F9F", "#279CAC", "#1A237E"), new Gradient("#FFAB91", "#CE93D8", "#311B92", "#1976D2", "#1A237E"), new Gradient("#DC5A7A", "#9739B2", "#311B92", "#6A189A", "#311B92"));
        GradientSet gradientSet4 = new GradientSet(new Gradient("#263238", "#CE93D8", "#0F284C", "#455A64", "#212121"), new Gradient("#CE93D8", "#F48FB1", "#2B1780", "#337283", "#003333"), new Gradient("#FFCDD2", "#F0F4C3", "#455A64", "#279CAA", "#546E7A"), new Gradient("#CAD3DA", "#E4E0D1", "#455A64", "#A3ADAC", "#546E7A"), new Gradient("#607D8B", "#FFAB91", "#3E2723", "#99615F", "#2F2A3D"), new Gradient("#474C61", "#E67864", "#333333", "#006064", "#221A45"));
        GradientSet gradientSet5 = new GradientSet(new Gradient("#263238", "#01579B", "#0F284C", "#195A64", "#212121"), new Gradient("#B39DDB", "#F48FB1", "#2B1780", "#50728C", "#263238"), new Gradient("#F3C276", "#BB89AE", "#575A8C", "#A3ADAC", "#BB89AE"), new Gradient("#BBDEFB", "#E4E0D1", "#455A64", "#A3ADAC", "#546E7A"), new Gradient("#F3C276", "#BB89AE", "#575A8C", "#586DA2", "#A9629A"), new Gradient("#474C61", "#E67864", "#333333", "#455A64", "#221A45"));
        GradientSet gradientSet6 = new GradientSet(new Gradient("#263238", "#01579B", "#0F284C", "#195A64", "#212121"), new Gradient("#B39DDB", "#F48FB1", "#2B1780", "#50728C", "#263238"), new Gradient("#F3C276", "#BB89AE", "#575A8C", "#A3ADAC", "#BB89AE"), new Gradient("#BBDEFB", "#E4E0D1", "#455A64", "#A3ADAC", "#546E7A"), new Gradient("#F3C276", "#BB89AE", "#575A8C", "#586DA2", "#A9629A"), new Gradient("#474C61", "#E67864", "#333333", "#455A64", "#221A45"));
        GradientSet gradientSet7 = new GradientSet(new Gradient("#607D8B", "#0D47A1", "#333333", "#311B92", "#2F2A3D"), new Gradient("#C096F5", "#CD89D4", "#2C3A95", "#1A658D", "#053440"), new Gradient("#FFF8E1", "#B0BEC5", "#455A64", "#004857", "#546E7A"), new Gradient("#607D8B", "#9FE0FF", "#455A64", "#5AADD2", "#546E7A"), new Gradient("#474C61", "#E57373", "#333333", "#303BA6", "#221A45"), new Gradient("#546E7A", "#DC5A7A", "#311B92", "#6A189A", "#311B92"));
        GradientSet gradientSet8 = new GradientSet(new Gradient("#1D272C", "#00796B", "#0F284C", "#30403D", "#212121"), new Gradient("#816581", "#FFF9C4", "#2b1780", "#337283", "#003333"), new Gradient("#FFCDD2", "#DCEDC8", "#48388f", "#1A8491", "#546E7A"), new Gradient("#B7B3B3", "#DAD4FF", "#1E3132", "#535D4B", "#546E7A"), new Gradient("#485352", "#E67864", "#3E2723", "#4E5A58", "#263230"), new Gradient("#353A52", "#6F297B", "#333333", "#006064", "#221A45"));
        GradientSet gradientSet9 = new GradientSet(new Gradient("#37474F", "#607D8B", "#263238", "#455A64", "#212121"), new Gradient("#5C6BC0", "#F48FB1", "#2B1780", "#607D8B", "#0A3880"), new Gradient("#CAD3DA", "#FFF59D", "#78909C", "#CFD8DC", "#F0F4C3"), new Gradient("#9FA8DA", "#FFFDE7", "#5C6BC0", "#BBDEFB", "#5C6BC0"), new Gradient("#78909C", "#FF7043", "#311B92", "#546E7A", "#311B92"), new Gradient("#546E7A", "#DC5A7A", "#311B92", "#6A189A", "#311B92"));
        GradientSet gradientSet1 = new GradientSet(new Gradient("#37474F", "#607D8B", "#263238", "#455A64", "#212121"), new Gradient("#5C6BC0", "#F48FB1", "#2B1780", "#607D8B", "#0A3880"), new Gradient("#CAD3DA", "#FFF59D", "#78909C", "#CFD8DC", "#F0F4C3"), new Gradient("#9FA8DA", "#FFFDE7", "#5C6BC0", "#BBDEFB", "#5C6BC0"), new Gradient("#78909C", "#FF7043", "#311B92", "#546E7A", "#311B92"), new Gradient("#546E7A", "#DC5A7A", "#311B92", "#6A189A", "#311B92"));

        this.gradientSets = new ArrayList<>(
                Arrays.asList(
                        gradientSet2, gradientSet,
                        gradientSet4, gradientSet5,
                        gradientSet6, gradientSet1,
                        gradientSet7, gradientSet9,
                        gradientSet8, gradientSet3));
        this.updateAllRanges();
    }

    public static float oscillationOffset() {
        long l = System.currentTimeMillis() % 20000L;
        float f = 0.0f;
        if (l >= 1000L) {
            if (l < 5000L) {
                f = MathUtil.map(l, 1000.0f, 5000.0f, 0.0f, 1.0f, true);
            } else if (l < 6000L) {
                f = 1.0f;
            } else if (l < 10000L) {
                f = MathUtil.map(l, 6000.0f, 10000.0f, 1.0f, 0.0f, true);
            } else if (l >= 11000L) {
                f = l < 15000L ? MathUtil.map(l, 11000.0f, 15000.0f, 0.0f, -1.0f, true) : (l < 16000L ? -1.0f : MathUtil.map(l, 16000.0f, 20000.0f, -1.0f, 0.0f, true));
            }
        }
        return f * 0.4f;
    }

    public void calcGradientByConditionAndTimeIndexWithOscillation(int n, int n2, boolean bl, Gradient gradient) {
        int n3;
        float f = GradientSetManager.oscillationOffset();
        if (bl) {
            f = 0.0f;
        }
        int n4 = 5;
        if (f >= 0.0f) {
            int n5;
            n3 = n5 = n2 + 1;
            if (n5 > 5) {
                n3 = 0;
            }
        } else {
            int n6;
            n3 = n6 = n2 - 1;
            if (n6 < 0) {
                n3 = n4;
            }
        }
        Gradient.lerp(this.gradientByConditionAndTimeIndex(n, n2), this.gradientByConditionAndTimeIndex(n, n3), Math.abs(f), gradient);
    }

    public Gradient gradientByConditionAndTimeIndex(int n, int n2) {
        return this.gradientSets.get(n).list().get(n2);
    }

    public GradientSet gradientSetByCondition(int n) {
        return this.gradientSets.get(n);
    }

    public void lerpUsingDayPercent(int n, int n2, float f, float f2, boolean bl, Gradient gradient) {
        this.gradientSetByCondition(n).lerpUsingDayPercent(f2, bl, this.scratch1);
        this.gradientSetByCondition(n2).lerpUsingDayPercent(f2, bl, this.scratch2);
        Gradient.lerp(this.scratch1, this.scratch2, f, gradient);
    }

    public void lerpUsingSlidingIndex(int n, int n2, float f, float f2, boolean bl, Gradient gradient) {
        this.gradientSetByCondition(n).lerpUsingSlidingIndex(f2, bl, this.scratch1);
        this.gradientSetByCondition(n2).lerpUsingSlidingIndex(f2, bl, this.scratch2);
        Gradient.lerp(this.scratch1, this.scratch2, f, gradient);
    }

    public void updateAllRanges() {
        for (GradientSet gradientSet : this.gradientSets) {
            gradientSet.updateRanges();
        }
    }
}


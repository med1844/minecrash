package engine.maths;

public class Perlin {
    public float persistence;
    public int Number_Of_Octaves;
    public Perlin(){
        persistence = 0.50f;    //持续度，持续度越大，振幅越大，对比度更搞
        Number_Of_Octaves = 4;  //倍频，叠加噪声函数的次数，使图像更细致
    }
 
    public double Noise(int x,int y)    // 随机数发生器，根据(x,y)获取一个初步噪声值，同样的输入有相同的输出
    {
        int n = x + y * 57;
        n = (n<<13) ^ n;
        return ( 1.0 - ( (n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0);
    }
 
    public double SmoothedNoise(int x, int y)   //光滑噪声
    {
        double corners = ( Noise(x-1, y-1)+Noise(x+1, y-1)+Noise(x-1, y+1)+Noise(x+1, y+1) ) / 16;
        double sides = ( Noise(x-1, y) +Noise(x+1, y) +Noise(x, y-1) +Noise(x, y+1) ) / 8;
        double center = Noise(x, y) / 4;
        return corners + sides + center;
    }
    public static double Cosine_Interpolate(double a,double b, double x)  // 余弦插值
    {
        double ft = x * 3.1415927;
        double f = (1 - Math.cos(ft)) * 0.5;
        return a*(1-f) + b*f;
    }
    public static double Linear_Interpolate(double a, double b, double x) //线性插值
    {
        return a*(1-x) + b*x;
    }
 
    public double InterpolatedNoise(float x,float y)   // 获取插值噪声
    {
        int integer_X = (int)x;
        float  fractional_X = x - integer_X;
        int integer_Y = (int)y;
        float fractional_Y = y - integer_Y;
        double v1 = SmoothedNoise(integer_X, integer_Y);
        double v2 = SmoothedNoise(integer_X + 1, integer_Y);
        double v3 = SmoothedNoise(integer_X, integer_Y + 1);
        double v4 = SmoothedNoise(integer_X + 1, integer_Y + 1);
        double i1 = Cosine_Interpolate(v1, v2, fractional_X);
        double i2 = Cosine_Interpolate(v3, v4, fractional_X);
        return Cosine_Interpolate(i1, i2, fractional_Y);
    }
 
    public double PerlinNoise(float x,float y)    // 最终调用：根据(x,y)获得其对应的PerlinNoise值
    {
        double noise = 0;
        double p = persistence;
        int n = Number_Of_Octaves;
        for(int i=0; i<n; i++)
        {
            double frequency = Math.pow(2,i);
            double amplitude = Math.pow(p,i);
            noise = noise + InterpolatedNoise((float)x * (float)frequency, (float)y * (float)frequency) * amplitude;
        }
 
        return noise;
    }
};



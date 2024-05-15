package mkp;


public class Individu implements Comparable<Individu> {
	
	int[] Individu;
	public float Fitness;

	
	//Constructeur
	public Individu(int[] solution) {
		this.Individu = solution;
		this.Fitness = 0;
	}

	public int[] GetIndividu() {
        return Individu;
    }
	
	public float GetFitness() {
        return Fitness;
    }
	
	public void SetIndividu(int[] solution) {
        this.Individu = solution;
    }
	
	public void SetFitness(float f) {
        this.Fitness = f;
    }

    @Override
    public int compareTo(Individu autreSolution) {
        // Comparaison bas�e sur la valeur de Fitness
        return Double.compare(autreSolution.Fitness, this.Fitness); //tri d�croissant
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Solution : {");
        for (int i=0; i<Individu.length; i++) {
            stringBuilder.append(" ").append(Individu[i]);
        }
        stringBuilder.append("}, Fitness : ").append(Fitness);
        return stringBuilder.toString();
    }
}
package mkp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Algo_Genetique {
	
	/*population = g�n�ration*/
    /*solution = individu = chromosome*/
    /*g�ne = une position de la solution*/
	
	public static Individu Recherche_AlgoGenetique(int nbrknapsacks, int Taille_Individu, int Taille_Gen, int Max_Gen, int Taille_Gen_Selected, float P_crossover, float P_mutation, List<Item> items, List<Knapsack> knapsacks) {
        ArrayList<Individu> Pop = new ArrayList<>();        
		ArrayList<Individu> Parents = new ArrayList<>();
		ArrayList<Individu> EnfantsC = new ArrayList<>();
        ArrayList<Individu> EnfantsM = new ArrayList<>();
        
        Pop = GenerationAleatoirePopulation(Taille_Individu, nbrknapsacks, Taille_Gen, items, knapsacks);
        //System.out.println("Population initiale avant evaluation:");
        //Afficher_Popoulation(Pop);
        Evaluation(Pop, Taille_Individu, items, knapsacks);
        //System.out.println("Population initiale apres evaluation:");
        //Afficher_Popoulation(Pop);
        
        for(int i=0; i<Max_Gen; i++) {
            //System.out.println("Generation " + (i+1));
        	
            Parents = Selection(Pop, Taille_Gen_Selected);
        	//System.out.println("Population selection�e:");
            //Afficher_Popoulation(Parents);
            
            EnfantsC = Croisement(Parents, Taille_Individu, P_crossover);
            //System.out.println("Croisement des parents:");
            //Afficher_Popoulation(EnfantsC);
            
            //System.out.println("Evaluation Croisement...");
            Evaluation(EnfantsC, Taille_Individu, items, knapsacks);
            
            EnfantsM = Mutation(EnfantsC, Taille_Individu, nbrknapsacks, P_mutation);
            //System.out.println("Mutation des parents:");
            //Afficher_Popoulation(EnfantsM);
            
            //System.out.println("Evaluation Mutation...");
            Evaluation(EnfantsM, Taille_Individu, items, knapsacks);
            
            Pop = Remplacement(Pop, EnfantsC, EnfantsM, Taille_Gen, Taille_Individu, items, knapsacks);
            //System.out.println("Population de la " + (i+1) + " generation:");
            //Afficher_Popoulation(Pop);
        }
        return MeilleurIndividu(Pop);
	}
	
	
	public static ArrayList<Individu> GenerationAleatoirePopulation(int Taille_Individu, int nbrknapsacks, int Taille_Gen, List<Item> items, List<Knapsack> knapsacks) {
		//permet de generer un ensemble de solution correcte
		ArrayList<Individu> pop_initiale = new ArrayList<>();
		int[] Solution = new int[Taille_Individu];
		Random rand = new Random();
		
		while (pop_initiale.size() != Taille_Gen) {
			//je genere une solution, je la rajoute a pop initiale que si elle est valide
			for (int i=0; i<Taille_Individu; i++) {
				Solution[i] = rand.nextInt(-1, nbrknapsacks); //G�n�re vecteur d'entier al�atoire entre -1 et Taille_individu
			}

			if (verificationSolution(Taille_Individu, Solution, items, knapsacks)) {
				Individu chromosome = new Individu(Solution.clone());
				if (!pop_initiale.contains(chromosome)) {
					pop_initiale.add(chromosome);
				}
			} else {
				// repair the solution then add to population: use reparerIndividu
				Individu chromosome = reparerIndividu(Taille_Individu, Solution.clone(), items, knapsacks);
				if (!pop_initiale.contains(chromosome)) {
					pop_initiale.add(chromosome);
				}
			}
		}
		

		return pop_initiale;
	}
	
	public static Individu reparerIndividu(int Taille_Individu, int[] solution, List<Item> items, List<Knapsack> knapsacks) {
		int[] knapsacksWeight = new int[knapsacks.size()];
		for (int i = 0; i < Taille_Individu; i++) {
			if (solution[i] != -1) {
				knapsacksWeight[solution[i]] += items.get(i).weight;
			}
		}

		for (int i = 0; i < knapsacksWeight.length; i++) {
			if (knapsacksWeight[i] > knapsacks.get(i).capacity) {
				for (int j = 0; j < Taille_Individu; j++) {
					if (solution[j] == i) {
						solution[j] = -1;
						knapsacksWeight[i] -= items.get(j).weight;
						if (knapsacksWeight[i] <= knapsacks.get(i).capacity) {
							break;
						}
					}
				}
			}
		}

		return new Individu(solution);
	}
	
	public static boolean verificationSolution(int Taille_Individu, int[] solution, List<Item> items, List<Knapsack> knapsacks) {
		//permet de verifier la validit� d'une solution
		boolean isValid = true;
		
		//on calcul le poids total de chaque knapsack             [0, 0, 0, 0]
		int[] KnapsacksWeight = new int[knapsacks.size()];    //  [-1, 3, 2, -1, 0, 1, 3]
		for (int i=0; i<Taille_Individu; i++) {
			if(solution[i] != -1) {
				KnapsacksWeight[solution[i]] += items.get(i).weight;
			}
		}
		//System.out.print("KnapsacksWeight : ");
		//Afficher_Solution(KnapsacksWeight);
		
		//on verifier qu'on a pas d�pass� la capacit� de chaque knapsacks
		for (int i=0; i<KnapsacksWeight.length; i++) {
			if (KnapsacksWeight[i] > knapsacks.get(i).capacity) {
				isValid = false;
			}
		}
		//System.out.println(" isValid: " + isValid);
		return isValid;
	}
	
	public static void Evaluation(ArrayList<Individu> pop, int Taille_Individu, List<Item> items, List<Knapsack> knapsacks) {
		/* * permet d'evaluer la qualit� de la solution
		 * La fonction d'evaluation fitness est a maximiser
	     * si solution correcte : 
	     * 		Fitness = valeur totale des objets rang�s dans les sacs a dos
		 * */
		for (Individu individu : pop) {
	        int[] solution = individu.Individu;
	        float totalValue = 0;
			for (int i=0; i<solution.length; i++) {
				if (solution[i] != -1) {
					totalValue += items.get(i).value;
				}
			}
	        //on met a jour fitness de la solution
	        individu.SetFitness(totalValue);
	    }
	}
	
	
	public static ArrayList<Individu> Selection(ArrayList<Individu> pop, int Taille_Gen_Selected) {
		//S�lection �litiste: les individus sont tri�s par ordre croissant selon Fitness, puis on selectionne les Taille_Gen_Selected meilleurs solutions
		ArrayList<Individu> pop_selected = new ArrayList<>();
		Collections.sort(pop);
		for(int i=0;i<pop.size() && pop_selected.size()<Taille_Gen_Selected; i++) {
    		if (!pop_selected.contains(pop.get(i))) {
    			pop_selected.add(pop.get(i));
    		}
	    }
    	return pop_selected;
	}
	
	
	public static ArrayList<Individu> Croisement(ArrayList<Individu> Parents, int Taille_individu, float P_crossover) {
	    // Croisement : monopoint
	    ArrayList<Individu> enfantC = new ArrayList<>();
	    float min = 0.0f;
        float max = 1.0f;
        Random random = new Random();
        
	    for(int i=0; i<Parents.size()-1; i+=2) {
	    	float r = min + random.nextFloat() * (max - min); //generer une proba 0<r<P_crossover
	    	//System.out.println("proba r : " + r + ", P_crossover: " + P_crossover);
	    	if (r < P_crossover) {
	    		int indice = (int) (Math.random() * (Taille_individu - 2) + 1);
		        //System.out.println("Indice de croisement : " + indice);

		        int[] C1 = new int[Taille_individu];
		        int[] C2 = new int[Taille_individu];

		        for(int j=0; j<Taille_individu; j++) { //Taille_individu + 1: pour ne pas ecraser la derniere ville
		            if(j<indice) {
		                C1[j] = Parents.get(i).Individu[j];
		                C2[j] = Parents.get(i + 1).Individu[j];
		            } else {
		                C1[j] = Parents.get(i + 1).Individu[j];
		                C2[j] = Parents.get(i).Individu[j];
		            }
		        }

		        Individu enfant1 = new Individu(C1);
		        enfantC.add(enfant1);
		        Individu enfant2 = new Individu(C2);
		        enfantC.add(enfant2);
		        
		        //System.out.println("Parent1 et Parent2 : ");
		        //Afficher_Individu(Parents.get(i));
		        //Afficher_Individu(Parents.get(i + 1));
		        //System.out.println("EnfantC1 et EnfantC2 : ");
		        //Afficher_Individu(enfant1);
		        //Afficher_Individu(enfant2);
		        
		        C1 = null; C2 = null; //liberer l'espace m�moire
	    	}	        
	    }
	    return enfantC;
	}
	
	
	
	public static ArrayList<Individu> Mutation(ArrayList<Individu> EnfantsC, int Taille_individu, int nbrknapsacks, float P_mutation) {
		//je choisis un indice et je le remplace par une autre valeur tq : 0 < val < nombre de sacs a dos
		ArrayList<Individu> EnfantsM = new ArrayList<>();
		float min = 0.0f;
        float max = 1.0f;
        Random random = new Random();
			
		for (int i=0; i<EnfantsC.size(); i++) {
			float r = min + random.nextFloat() * (max - min); //g�n�rer une proba 0<r<P_crossover
	    	//System.out.println("proba r : " + r + ", P_mutation: " + P_mutation);
	    	if (r < P_mutation) {
	    		int [] new_enfant = Arrays.copyOf(EnfantsC.get(i).Individu, EnfantsC.get(i).Individu.length);
		        
				int indice = random.nextInt(Taille_individu - 1); //on choisit le gene
				//System.out.println("Indice de mutation : " + indice);
				
				int new_knapsack = random.nextInt(nbrknapsacks); //on choisit la mutation
				//System.out.println("nouveau knapsack : " + new_knapsack);
				
				//System.out.println("EnfantsM avant mutation : ");
				//Afficher_Individu(EnfantsC.get(i));
				
				new_enfant[indice] = new_knapsack; //on effectue la mutation
				Individu enfantm = new Individu(new_enfant);
				
				//System.out.println("EnfantsM apres mutation : ");
				//Afficher_Individu(enfantm);
			        
				EnfantsM.add(enfantm);
	    	}
		}
		return EnfantsM;
	}
	
	 
	public static ArrayList<Individu> Remplacement(ArrayList<Individu> Pop, ArrayList<Individu> EnfantsC, ArrayList<Individu> EnfantsM, int Taille_Gen, int Taille_Individu, List<Item> items, List<Knapsack> knapsacks) {
		//on remplace les individus moins bons par les plus bons
		//Concat�ner les enfants avec la population actuelle
	    Pop.addAll(EnfantsC);
	    Pop.addAll(EnfantsM);
	    Collections.sort(Pop); //trier la population selon la valeur de fitness (meilleur individu d'abord)
	    //System.out.println("Remplacement .... Population + croisement + mutation apres sort:");
        //Afficher_Popoulation(Pop);
        
	    //s�lection des meilleurs individus
	    ArrayList<Individu> best_pop = new ArrayList<>();
	    for (int i=0; i<Pop.size() && best_pop.size()<Taille_Gen; i++) {
	        if (verificationSolution(Taille_Individu, Pop.get(i).Individu, items, knapsacks) && !best_pop.contains(Pop.get(i))) {
	            best_pop.add(Pop.get(i));
	        }
	    }
	    //v�rifier si la meilleure population est vide ou pas
	    if (best_pop.isEmpty()) {
	        System.out.println("Aucun individu valide trouv� apr�s le remplacement...");
	    }
	    return best_pop;
	}

	
	
	public static Individu MeilleurIndividu(ArrayList<Individu> pop) {
		//Evaluation(pop, Taille_individu, Distances);
    	return (pop.remove(0));
	}
	
	
	public static void Afficher_Individu(Individu sol) {
        if (sol.Individu == null) {
            System.out.println("Il n'y a pas de solution!");
            return;
        } else {
        	System.out.print("Solution : { ");
            for (int elem : sol.Individu) {
                System.out.print(elem + " ");
            }
            System.out.println("}, Fitness : " + sol.Fitness);
        }
    }
	
	
	public static void Afficher_Popoulation(ArrayList<Individu> pop) {
        if (pop.size() == 0) {
            System.out.println("La population ne contient aucun individu!");
            return;
        } else {
        	System.out.println("Population :");
            for (Individu chromosome : pop) {
            	Afficher_Individu(chromosome);
            }
            System.out.println("Fin...");
        }
    }
	
	public static void Afficher_Solution(int[] sol) {
        if (sol== null) {
            System.out.println("Il n'y a pas de solution!");
            return;
        } else {
        	System.out.print("{ ");
            for (int elem : sol) {
                System.out.print(elem + " ");
            }
            System.out.print("}");
        }
    }
	
}

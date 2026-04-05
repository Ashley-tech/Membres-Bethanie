<?php
	require_once("../modeles/connexion.php");
	require_once("../modeles/compte.php");
	
	if (!empty($_POST["login"]) && !empty($_POST["pwd"]) && !empty($_POST["pwdr"]) && !empty($_POST["profil"])){
		$login = str_replace("'","''",$_POST["login"]);
        if ($_POST["pwd"] != $_POST["pwdr"]){
            echo "Mots de passes différents";
        } else {
            $pwd= str_replace("'","''",$_POST["pwd"]);
		    $profil= $_POST["profil"];
		
		    $coBd = new Connexion("paris-bethanie_membre_test");
		    $co = $coBd->connexion();
            $result = mysqli_query($co, "SELECT count(*) as count, statut_compte FROM compte WHERE login='$login'") or die("Erreur requête");
            while ($line = mysqli_fetch_assoc($result)) {
                $count = $line['count'];
                $statut = $line['statut_compte'];
            }

            if ($count == 1) {
                echo "Compte existant";
            }else{
                $m = new Compte($co,$login,$pwd,$profil);
                $m->connexion();
                
                echo "Succès";
            }
        }
	} else {
        echo "Champ vide";
    }
?>
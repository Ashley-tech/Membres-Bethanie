<?php
	require_once("../modeles/connexion.php");
	require_once("../modeles/compte.php");
	
	if (!empty($_POST["login"])){
		$login = str_replace("'","''",$_POST['login']);
		
		$coBd = new Connexion("paris-bethanie_membre_test");
		$co = $coBd->connexion() or die("Erreur de connexion !");
		
		$result = mysqli_query($co, "SELECT count(*) as count, id_compte, statut_compte, mdp FROM compte WHERE login='$login'") or die("Erreur requête");
        while ($line = mysqli_fetch_assoc($result)) {
            $count = $line['count'];
            $statut = $line['statut_compte'];
            $pwd = $line['mdp'];
		$id = $line['id_compte'];
        }
 
		if ($count == 1 && $statut != 'Supprimé') {
            echo $id;
		}else{
			echo "Login non trouvé";
		}
	} else {
        echo "Champ vide";
    }
?>
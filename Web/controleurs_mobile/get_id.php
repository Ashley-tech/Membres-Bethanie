<?php
	require_once("../modeles/connexion.php");
	$coBd = new Connexion("paris-bethanie_membre_test");
	$co = $coBd->connexion();
	$c = "";
	if (!empty($_GET['pseudo']) && (!empty($_GET['pwd']))) {
		$id = str_replace("'","''",$_GET['pseudo']);
		$m = str_replace("'","''",$_GET['pwd']);
		$result = mysqli_query($co, "SELECT id_compte FROM compte WHERE login='$id' AND mdp='$m'") or die("Erreur requête");
            	if (mysqli_num_rows($result) == 1) {
					while ($line = mysqli_fetch_assoc($result)) {
						$c = $line['id_compte'];
					}
				} else {
					$c = 0;
				}
	} else {
		$c = 0;
	}
	echo $c;
?>
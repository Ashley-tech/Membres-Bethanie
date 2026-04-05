<?php
	require_once("../modeles/connexion.php");
	$coBd = new Connexion("paris-bethanie_membre_test");
	$co = $coBd->connexion();
	$c = "";
	if (!empty($_GET['profil'])) {
		$id = $_GET['profil'];
		$result = mysqli_query($co, "SELECT profil_compte FROM compte WHERE id_compte=$id") or die("Erreur requête");
            	if (mysqli_num_rows($result) == 1) {
					while ($line = mysqli_fetch_assoc($result)) {
						$c = $line['profil_compte'];
					}
				} else {
					$c = "Echec";
				}
	} else {
		$c = "Echec";
	}
	echo $c;
?>
<?php
    	$co =mysqli_connect("mysql-paris-bethanie.alwaysdata.net","299683","Gambetta","paris-bethanie_membre_test") or die("Erreur de connexion !");
    	$response = array();
	if (!empty ($_GET['membre'])) {
		$c = $_GET['membre'];
    		if ($co){
        		$sth = mysqli_query($co, "SELECT date_naissance, mel, telfixe, telportable, telfax, telfixepro, telportablepro, telfaxpro, adresse, adresse_comp, cp, ville, quartierville, numboite_appt FROM personne WHERE id_personne=$c");
        		if (mysqli_num_rows($sth) > 0){
            			$rows = array();
            			$response["membre"] = array();
            			while($r = mysqli_fetch_array($sth)) {
                			if (empty($r['date_naissance'])) {
						$rows["date"] = "Non renseigné";
					} else {
						$rows["date"] = $r['date_naissance'];
					}

					if (empty($r['mel'])) {
						$rows["mail"] = "Non renseigné";
					} else {
						$rows["mail"] = $r['mel'];
					}

					if (empty($r['telfixe'])) {
						$rows["telephone_fixe_perso"] = "Non renseigné";
					} else {
						$rows["telephone_fixe_perso"] = $r['telfixe'];
					}

					if (empty($r['telportable'])) {
						$rows["telephone_portable_perso"] = "Non renseigné";
					} else {
						$rows["telephone_portable_perso"] = $r['telportable'];
					}

					if (empty($r['telfax'])) {
						$rows["telephone_fax_perso"] = "Non renseigné";
					} else {
						$rows["telephone_fax_perso"] = $r['telfax'];
					}

					if (empty($r['telfixepro'])) {
						$rows["telephone_fixe_pro"] = "Non renseigné";
					} else {
						$rows["telephone_fixe_pro"] = $r['telfixepro'];
					}

					if (empty($r['telportablepro'])) {
						$rows["telephone_portable_pro"] = "Non renseigné";
					} else {
						$rows["telephone_portable_pro"] = $r['telportablepro'];
					}

					if (empty($r['telfaxpro'])) {
						$rows["telephone_fax_pro"] = "Non renseigné";
					} else {
						$rows["telephone_fax_pro"] = $r['telfaxpro'];
					}

					if (empty($r['adresse'])) {
						$rows["adresse"] = "Non renseigné";
					} else {
						$rows["adresse"] = $r['adresse'];
					}

					if (empty($r['adresse_comp'])) {
						$rows["complement_adresse"] = "Non renseigné";
					} else {
						$rows["complement_adresse"] = $r['adresse_comp'];
					}

					if (empty($r['cp'])) {
						$rows["code_postal"] = "Non renseigné";
					} else {
						$rows["code_postal"] = $r['cp'];
					}

					if (empty($r['ville'])) {
						$rows["ville"] = "Non renseigné";
					} else {
						$rows["ville"] = $r['ville'];
					}

					if (empty($r['quartierville'])) {
						$rows["quartier"] = "Non renseigné";
					} else {
						$rows["quartier"] = $r['quartierville'];
					}

					if (empty($r['numboite_appt'])) {
						$rows["boite_appart"] = "Non renseigné";
					} else {
						$rows["boite_appart"] = $r['numboite_appt'];
					}

					$sth1 = mysqli_query($co, "SELECT libelle FROM lienpersonne JOIN lienparente ON (lien = id_lien) WHERE personne=$c");
					$lignes = array();
					$rows['liens']=array();
					
					if (mysqli_num_rows($sth1) > 0){
						while($e = mysqli_fetch_array($sth1)){
							$lignes['lien'] = $e['libelle'];
							array_push($rows['liens'],$lignes);
						}
					} else {
						$lignes['lien'] = "Aucun";
						array_push($rows['liens'],$lignes);
					}
                			array_push($response["membre"],$rows);
            			}
            			$response["success"]=1;
        		} else {
            			$response["success"]=0;
            			$response["message"]="L'identifiant ne correspond à aucun membre !";
        		}
    		} else {
			$response["success"]=0;
            		$response["message"]="Aucune connexion !";
		}
	} else {
		$response["success"]=0;
            	$response["message"]="L'identifiant du membre n'est pas renseigné !";
	}
    	echo json_encode($response);
?>
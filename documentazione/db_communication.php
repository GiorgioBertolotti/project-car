<?php

define('db_name','');
define('db_user','');
define('db_host','');

// Load Request
$api_method = isset($_POST['api_method']) ? $_POST['api_method'] : '';
$api_data = isset($_POST['api_data']) ? $_POST['api_data'] : '';

// Validate Request
if (empty($api_method) || empty($api_data)) {
    API_Response(true, 'Invalid Request');
}
if (!function_exists($api_method)) {
    API_Response(true, 'API Method Not Implemented');
}

// Call API Method
call_user_func($api_method, $api_data);

// Helper Function

function API_Response($isError, $errorMessage, $responseData = ''){
    exit(json_encode(array(
        'IsError' => $isError,
        'ErrorMessage' => $errorMessage,
        'ResponseData' => $responseData
    )));
}

// Funzioni

function registerUser($api_data){
    $conn = mysql_connect($db_host, $db_user);
    if(checkConnection($conn,$db_name)){
		if(checkUniqueness($api_data,$conn)){
			// Decode register data
			$login_data = json_decode($api_data);
			// Insert
			$query = "INSERT INTO User (Name, Surname, Mobile, Password, Type_id) VALUES ('".$login_data->name."','".$login_data->surname."','".$login_data->mobile."','".$login_data->password."','NULL')";
			$result = mysql_query($query,$conn);
			if($result == true)
				echo "ok";
		}
	}
}

function loginUser($api_data){
    $conn = mysql_connect($db_host, $db_user);
    if(checkConnection($conn,$db_name)){
		$id = getIDbyMobile($api_data,$conn);
		$login_data = json_decode($api_data);
		$query = "SELECT * FROM User WHERE id = '".$id."'";
		$result = mysql_query($query,$conn);
		while ($row = mysql_fetch_array($result)) {
			if($row['password']==$login_data->password){
				$json_result = json_encode($row);
				if($json_result!=false){
					echo $json_result;
				}
			}
		}
	}
}

function logoutUser($api_data){
    $conn = mysql_connect($db_host, $db_user);
    if(checkConnection($conn,$db_name)){
		$id = getIDbyMobile($api_data,$conn);
		$logout_data = json_decode($api_data);
		$queryp = "DELETE FROM User_Position WHERE User_id = '".$id."'";
		mysql_query($queryd,$conn);
		$queryc = "DELETE FROM User_City WHERE User_id = '".$id."'";
		mysql_query($queryc,$conn);
		$queryt = "UPDATE User SET Type_id = 'NULL' WHERE id = '".$id."'";
		mysql_query($queryt,$conn);
	}
}

function User_City($api_data){
    $conn = mysql_connect($db_host, $db_user);
    if(checkConnection($conn,$db_name)){
		$id = getIDbyMobile($api_data,$conn);
		$data = json_decode($api_data);
		$query = "INSERT INTO User_City (User_ID,City_ID) VALUES ('".$id."','".$data->city_id."')";
		$result = mysql_query($query,$conn);
		if($result == TRUE)
			echo "ok";
	}
}

function User_Type($api_data){
    $conn = mysql_connect($db_host, $db_user);
    if(checkConnection($conn,$db_name)){
		$id = getIDbyMobile($api_data,$conn);
		$data = json_decode($api_data);
		$query = "SELECT * FROM Type WHERE Type = '".$data->type."'";
		$result = mysql_query($query,$conn);
		while ($row = mysql_fetch_array($result)) {
			$query2 = "UPDATE User SET Type_id = '".$row['id']."' WHERE id = '".$id."'";
			mysql_query($query2,$conn);
		}
	}
}

function getCities($api_data){
    $conn = mysql_connect($db_host, $db_user);
    if(checkConnection($conn,$db_name)){
		$query = "SELECT * FROM City";
		$result = mysql_query($query,$conn);
		$json_result = json_encode($result);
		if($json_result!=false){
			echo $json_result;
		}
	}
}

function getAS($api_data){
	$conn = mysql_connect($db_host, $db_user);
	if(checkConnection($conn,$db_name)){
		$data = json_decode($api_data);
		$query = "SELECT * FROM Type WHERE Type = 'autostoppista'";
		$tipi = mysql_query($query,$conn);
		while ($tipo = mysql_fetch_array($tipi)){
			$query2 = "SELECT * FROM User WHERE Type_id = '".$tipo['id']."'";
			$utenti = mysql_query($query2,$conn);
			while ($utente = mysql_fetch_array($utenti)){
				$query3 = "SELECT * FROM User_City WHERE User_id = '".$utente['id']."'";
				$idcittap = mysql_query($query3,$conn);
				while ($idcitta = mysql_fetch_array($idcittap)){
					$query4 = "SELECT * FROM City WHERE id = '".$idcitta['City_id']."'";
					$cittap = mysql_query($query4,$conn);
					while ($citta = mysql_fetch_array($cittap)){
						$utente['City_Name'] = $citta['Name'];
						$utente['City_Province'] = $citta['Province'];
					}
				}
			}
			$json_result = json_encode($utenti);
			if($json_result!=false){
				echo $json_result;
			}
		}		
	}
}

function getActiveUsers($api_data){
	$conn = mysql_connect($db_host, $db_user);
	if(checkConnection($conn,$db_name)){
		$query = "SELECT * FROM User WHERE Type_id != 'NULL'";
		$utenti = mysql_query($query,$conn);
		while($utente = mysql_fetch_array($utenti)){
			$query2 = "SELECT * FROM User_Position WHERE User_id = '".$utente['id']."'";
			$posizioni = mysql_query($query2);
			while($posizione = mysql_fetch_array($posizioni))
			{
				$utente['Longitude'] = $posizione['Longitude'];
				$utente['Latitude'] = $posizione['Latitude'];
				$utente['Date'] = $posizione['Date'];
			}
		}
		$json_result = json_encode($utenti);
		if($json_result!=false){
			echo $json_result;
		}
	}
}

function removeUser_City($api_data){
	$conn = mysql_connect($db_host, $db_user);
	if(checkConnection($conn,$db_name)){
		$id = getIDbyMobile($api_data,$conn);
		$query = "DELETE FROM User_City WHERE User_id = '".$id."'";
		if(mysql_query($query)){
			echo "ok";
		}
	}
}

function removeUser_Type($api_data){
	$conn = mysql_connect($db_host, $db_user);
	if(checkConnection($conn,$db_name)){
		$id = getIDbyMobile($api_data,$conn);
		$query = "UPDATE User SET Type_id = 'NULL' WHERE id = '".$id."'";
		if(mysql_query($query)){
			$query2 = "DELETE FROM User_Position WHERE User_id = '".$id."'";
			if(mysql_query($query2)){
				echo "ok";
			}
		}
	}
}

function checkConnection($conn,$db_name){
	$errore = false;
	if ($conn == FALSE)
	{
		API_Response(true,"Errore nella connessione.");
		$errore = true;
	}
    $ris = mysql_select_db($db_name);
    if ($ris == FALSE)
	{
		API_Response(true,"Errore nella connessione col database.");
		$errore = true;
	}
	return $errore;
}

function getIDbyMobile($api_data,$conn){
	if(!checkUniqueness($api_data,$conn){
		$data_ = json_decode($api_data);
		$query = "SELECT id FROM User WHERE Mobile = '".$data->mobile."'";
		$result = mysql_query($query,$conn);
		while ($riga = mysql_fetch_array($result)) {
			return $riga['id'];
		}
	}
}

function checkUniqueness($api_data,$conn){
	// Decode user data
	$user_data = json_decode($api_data);
	// Istruzione
	$query = "SELECT count(*) FROM User WHERE mobile = '".$user_data->mobile."'";
	if(mysql_query($query,$conn)=='0'){
		return true;
	} else{
		return false;
	}
}
?>
CREATE TABLE `experiments` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`date` timestamp NOT NULL DEFAULT current_timestamp(),
	`name` varchar(255) NOT NULL,
	`comment` text NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4
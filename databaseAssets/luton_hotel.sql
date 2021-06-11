-- phpMyAdmin SQL Dump
-- version 5.0.4
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: May 16, 2021 at 11:07 AM
-- Server version: 10.4.17-MariaDB
-- PHP Version: 8.0.2

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `luton_hotel`
--

-- --------------------------------------------------------

--
-- Table structure for table `billing_info`
--

CREATE TABLE `billing_info` (
  `bill_id` int(11) NOT NULL,
  `booking_id` int(11) NOT NULL,
  `amount` float NOT NULL,
  `payed_on` datetime DEFAULT current_timestamp(),
  `status` char(1) NOT NULL DEFAULT 'N'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `booking_info`
--

CREATE TABLE `booking_info` (
  `booking_id` int(11) NOT NULL,
  `customer_id` varchar(20) NOT NULL,
  `booking_on` datetime NOT NULL DEFAULT current_timestamp(),
  `status` int(11) NOT NULL DEFAULT 1,
  `checkin_on` datetime DEFAULT NULL,
  `checkout_on` datetime NOT NULL,
  `total_guests` int(11) NOT NULL,
  `room_type_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Triggers `booking_info`
--
DELIMITER $$
CREATE TRIGGER `onAfterBookingUpdate` AFTER UPDATE ON `booking_info` FOR EACH ROW IF (NEW.status = 2) 
THEN
    UPDATE room
    SET
    	room.booking_id = NEW.booking_id
    WHERE
    	room.room_id = (SELECT room_id from room where room_type_id = NEW.room_type_id  AND booking_id IS NULL LIMIT 1);
        
    	
ELSEIF (NEW.status = 3)
THEN
	UPDATE room
    SET
    	room.booking_id = NULL
    WHERE
    	room.booking_id = NEW.booking_id;
    
    INSERT into billing_info(booking_id, amount) VALUES(NEW.booking_id, (SELECT COALESCE(sum(amount),0) from service_charge where booking_id = NEW.booking_id));
    
	        
END IF
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `onBeforeBookingUpdate` BEFORE UPDATE ON `booking_info` FOR EACH ROW IF (NEW.status = 2)
THEN
	set NEW.checkin_on = CURRENT_TIMESTAMP;
ELSEIF (NEW.status = 3)
THEN
	set NEW.checkout_on = CURRENT_TIMESTAMP;
END IF
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `corporate_client`
--

CREATE TABLE `corporate_client` (
  `user_id` varchar(20) NOT NULL,
  `corporate_name` varchar(25) NOT NULL,
  `total_amount` float NOT NULL DEFAULT 0,
  `role` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `receptionist`
--

CREATE TABLE `receptionist` (
  `user_id` varchar(20) NOT NULL,
  `reception_code` varchar(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `regular_customer`
--

CREATE TABLE `regular_customer` (
  `user_id` varchar(20) NOT NULL,
  `credit_card_number` char(16) NOT NULL,
  `expiry_date` date NOT NULL,
  `card_type` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `room`
--

CREATE TABLE `room` (
  `room_id` int(11) NOT NULL,
  `room_type_id` int(11) NOT NULL,
  `booking_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `room`
--

INSERT INTO `room` (`room_id`, `room_type_id`, `booking_id`) VALUES
(1, 1, NULL),
(2, 2, NULL),
(3, 3, NULL),
(4, 1, NULL),
(5, 2, NULL),
(6, 3, NULL),
(7, 1, NULL),
(8, 2, NULL),
(9, 3, NULL),
(10, 1, NULL),
(11, 1, NULL),
(12, 3, NULL),
(13, 3, NULL),
(14, 2, NULL),
(15, 1, NULL);

--
-- Triggers `room`
--
DELIMITER $$
CREATE TRIGGER `onAddRoom` AFTER INSERT ON `room` FOR EACH ROW update room_allocation_status
set total_rooms = total_rooms + 1
where room_type_id = NEW.room_type_id
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `onRoomUpdate` AFTER UPDATE ON `room` FOR EACH ROW UPDATE room_allocation_status as ras
   	SET
		ras.booked_count = IF(NEW.booking_id IS NULL, ras.booked_count-1, ras.booked_count+1)
	WHERE
    	ras.room_type_id = NEW.room_type_id
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `room_allocation_status`
--

CREATE TABLE `room_allocation_status` (
  `room_type_id` int(11) NOT NULL,
  `room_type` varchar(20) NOT NULL,
  `booked_count` int(11) NOT NULL DEFAULT 0,
  `total_rooms` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `room_allocation_status`
--

INSERT INTO `room_allocation_status` (`room_type_id`, `room_type`, `booked_count`, `total_rooms`) VALUES
(1, 'SINGLE', 0, 6),
(2, 'TWIN', 0, 4),
(3, 'DOUBLE', 1, 5);

-- --------------------------------------------------------

--
-- Table structure for table `service_charge`
--

CREATE TABLE `service_charge` (
  `service_charge_id` int(11) NOT NULL,
  `charged_by_id` varchar(20) NOT NULL,
  `service_info` varchar(50) NOT NULL,
  `booking_id` int(11) NOT NULL,
  `amount` float NOT NULL,
  `charged_on` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `user_id` varchar(20) NOT NULL,
  `password` varchar(15) NOT NULL,
  `first_name` varchar(30) NOT NULL,
  `last_name` varchar(30) NOT NULL,
  `age` tinyint(4) NOT NULL,
  `gender` char(1) NOT NULL,
  `user_type` char(2) NOT NULL,
  `email_id` varchar(35) NOT NULL,
  `phone_number` char(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `billing_info`
--
ALTER TABLE `billing_info`
  ADD PRIMARY KEY (`bill_id`),
  ADD KEY `booking_id` (`booking_id`);

--
-- Indexes for table `booking_info`
--
ALTER TABLE `booking_info`
  ADD PRIMARY KEY (`booking_id`),
  ADD KEY `customer_id` (`customer_id`),
  ADD KEY `room_type_id` (`room_type_id`);

--
-- Indexes for table `corporate_client`
--
ALTER TABLE `corporate_client`
  ADD PRIMARY KEY (`user_id`);

--
-- Indexes for table `receptionist`
--
ALTER TABLE `receptionist`
  ADD PRIMARY KEY (`user_id`);

--
-- Indexes for table `regular_customer`
--
ALTER TABLE `regular_customer`
  ADD PRIMARY KEY (`user_id`);

--
-- Indexes for table `room`
--
ALTER TABLE `room`
  ADD PRIMARY KEY (`room_id`),
  ADD KEY `booking_id` (`booking_id`),
  ADD KEY `room_type_id` (`room_type_id`);

--
-- Indexes for table `room_allocation_status`
--
ALTER TABLE `room_allocation_status`
  ADD PRIMARY KEY (`room_type_id`);

--
-- Indexes for table `service_charge`
--
ALTER TABLE `service_charge`
  ADD PRIMARY KEY (`service_charge_id`),
  ADD KEY `booking_id` (`booking_id`),
  ADD KEY `charged_by_id` (`charged_by_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `billing_info`
--
ALTER TABLE `billing_info`
  MODIFY `bill_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `booking_info`
--
ALTER TABLE `booking_info`
  MODIFY `booking_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `room`
--
ALTER TABLE `room`
  MODIFY `room_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `service_charge`
--
ALTER TABLE `service_charge`
  MODIFY `service_charge_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `billing_info`
--
ALTER TABLE `billing_info`
  ADD CONSTRAINT `billing_info_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `booking_info` (`booking_id`);

--
-- Constraints for table `booking_info`
--
ALTER TABLE `booking_info`
  ADD CONSTRAINT `booking_info_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `user` (`user_id`),
  ADD CONSTRAINT `booking_info_ibfk_2` FOREIGN KEY (`room_type_id`) REFERENCES `room_allocation_status` (`room_type_id`);

--
-- Constraints for table `corporate_client`
--
ALTER TABLE `corporate_client`
  ADD CONSTRAINT `corporate_client_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `receptionist`
--
ALTER TABLE `receptionist`
  ADD CONSTRAINT `receptionist_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `regular_customer`
--
ALTER TABLE `regular_customer`
  ADD CONSTRAINT `regular_customer_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `room`
--
ALTER TABLE `room`
  ADD CONSTRAINT `room_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `booking_info` (`booking_id`),
  ADD CONSTRAINT `room_ibfk_2` FOREIGN KEY (`room_type_id`) REFERENCES `room_allocation_status` (`room_type_id`);

--
-- Constraints for table `service_charge`
--
ALTER TABLE `service_charge`
  ADD CONSTRAINT `service_charge_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `booking_info` (`booking_id`),
  ADD CONSTRAINT `service_charge_ibfk_2` FOREIGN KEY (`charged_by_id`) REFERENCES `user` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

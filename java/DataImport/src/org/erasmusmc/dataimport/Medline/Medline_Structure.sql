-- phpMyAdmin SQL Dump
-- version 2.11.8.1deb1ubuntu0.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jul 06, 2010 at 02:34 PM
-- Server version: 5.0.67
-- PHP Version: 5.2.6-2ubuntu4.3

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `medline_2010`
--

-- --------------------------------------------------------

--
-- Table structure for table `medline_article_language`
--

CREATE TABLE IF NOT EXISTS `medline_article_language` (
  `pmid` int(11) NOT NULL default '0',
  `language` varchar(100) default NULL,
  PRIMARY KEY  (`pmid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `medline_article_publication_type`
--

CREATE TABLE IF NOT EXISTS `medline_article_publication_type` (
  `pmid` int(11) NOT NULL default '0',
  `publication_type` varchar(100) default NULL,
  PRIMARY KEY  (`pmid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `medline_author`
--

CREATE TABLE IF NOT EXISTS `medline_author` (
  `author_order` tinyint(4) NOT NULL,
  `pmid` int(11) NOT NULL default '0',
  `last_name` text,
  `fore_name` varchar(255) default NULL,
  `first_name` varchar(255) default NULL,
  `middle_name` varchar(255) default NULL,
  `initials` varchar(100) default NULL,
  `suffix` varchar(100) default NULL,
  `affiliation` text,
  `collective_name` text,
  `dates_associated_with_name` text,
  `name_qualifier` text,
  `other_information` text,
  `title_associated_with_name` text,
  `author_valid_yn` char(1) default 'Y',
  PRIMARY KEY  (`pmid`,`author_order`),
  KEY `pmid` (`pmid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `medline_chemical_list`
--

CREATE TABLE IF NOT EXISTS `medline_chemical_list` (
  `pmid` int(11) NOT NULL default '0',
  `registry_number` varchar(20) default NULL,
  `name_of_substance` text NOT NULL,
  KEY `idx_m_chem_pmid` (`pmid`),
  KEY `registry_number` (`registry_number`),
  KEY `name_of_substance_2` (`name_of_substance`(1000))
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `medline_citation`
--

CREATE TABLE IF NOT EXISTS `medline_citation` (
  `pmid` int(11) NOT NULL default '0',
  `date_created` date NOT NULL default '0000-00-00',
  `date_completed` date default NULL,
  `date_revised` date default NULL,
  `issn` varchar(9) default NULL,
  `issn_linking` varchar(9) default NULL,
  `volume` varchar(100) default NULL,
  `issue` varchar(100) default NULL,
  `pub_date_year` varchar(4) default NULL,
  `pub_date_month` varchar(20) default NULL,
  `pub_date_day` char(2) default NULL,
  `pub_date` date default NULL,
  `article_date_day` char(2) default NULL,
  `article_date_month` char(2) default NULL,
  `article_date_year` varchar(4) default NULL,
  `article_date_type` varchar(11) default NULL,
  `pub_date_season` varchar(10) default NULL,
  `medline_date` varchar(100) default NULL,
  `journal_print_yn` char(1) default NULL,
  `coden` varchar(100) default NULL,
  `journal_title` text,
  `iso_abbreviation` varchar(50) default NULL,
  `article_title` text NOT NULL,
  `start_page` varchar(10) default NULL,
  `end_page` varchar(10) default NULL,
  `medline_pgn` varchar(100) default NULL,
  `abstract_text` text,
  `copyright_info` text,
  `article_affiliation` text,
  `article_author_list_comp_yn` char(1) default 'Y',
  `data_bank_list_comp_yn` char(1) default 'Y',
  `grantlist_complete_yn` char(1) default 'Y',
  `vernacular_title` text,
  `date_of_electronic_publication` date default NULL,
  `elec_pub_official_date_yn` char(1) default 'N',
  `country` varchar(50) default NULL,
  `medline_ta` text NOT NULL,
  `nlm_unique_id` varchar(20) default NULL,
  `xml_file_name` text NOT NULL,
  `number_of_references` int(11) default NULL,
  `keyword_list_owner` varchar(30) default NULL,
  `pub_model` varchar(20) default NULL,
  `cited_medium` varchar(15) default NULL,
  `issn_type` varchar(15) default NULL,
  `citation_owner` varchar(30) default 'NLM',
  `citation_status` varchar(50) default NULL,
  `elocationid` varchar(100) default NULL,
  `elocationid_eidtype` varchar(3) default NULL,
  `elocationid_validyn` varchar(1) default NULL,
  PRIMARY KEY  (`pmid`),
  KEY `idx_m_c_date` (`date_created`,`pmid`),
  KEY `idx_m_c_journal` (`nlm_unique_id`,`volume`,`issue`,`pmid`),
  KEY `pub_date_year` (`pub_date_year`),
  KEY `xml_file_name` (`xml_file_name`(100)),
  KEY `pub_date` (`pub_date`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `medline_citation_other_abstract`
--

CREATE TABLE IF NOT EXISTS `medline_citation_other_abstract` (
  `pmid` int(11) NOT NULL default '0',
  `type` varchar(100) default NULL,
  `copyright_info` text,
  `abstract_text` text,
  PRIMARY KEY  (`pmid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `medline_citation_other_id`
--

CREATE TABLE IF NOT EXISTS `medline_citation_other_id` (
  `pmid` int(11) NOT NULL default '0',
  `source` varchar(100) default NULL,
  `other_id` text,
  KEY `idx_other_id_pmid` (`pmid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `medline_citation_subsets`
--

CREATE TABLE IF NOT EXISTS `medline_citation_subsets` (
  `pmid` int(11) NOT NULL default '0',
  `citation_subset` text NOT NULL,
  PRIMARY KEY  (`pmid`,`citation_subset`(100))
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `medline_comments_corrections`
--

CREATE TABLE IF NOT EXISTS `medline_comments_corrections` (
  `pmid` int(11) NOT NULL default '0',
  `ref_pmid` int(11) default NULL,
  `note` text,
  `type` varchar(100) default NULL,
  `ref_source` text,
  KEY `idx_comments_pmid` (`pmid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `medline_data_bank`
--

CREATE TABLE IF NOT EXISTS `medline_data_bank` (
  `pmid` int(11) NOT NULL default '0',
  `data_bank_name` varchar(100) NOT NULL default '',
  `accession_number` varchar(100) NOT NULL default '',
  PRIMARY KEY  (`pmid`,`data_bank_name`,`accession_number`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `medline_general_note`
--

CREATE TABLE IF NOT EXISTS `medline_general_note` (
  `pmid` int(11) NOT NULL default '0',
  `owner` varchar(100) default NULL,
  `general_note` text,
  PRIMARY KEY  (`pmid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `medline_gene_symbol_list`
--

CREATE TABLE IF NOT EXISTS `medline_gene_symbol_list` (
  `pmid` int(11) NOT NULL default '0',
  `gene_symbol` varchar(100) NOT NULL default '',
  PRIMARY KEY  (`pmid`,`gene_symbol`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `medline_grant`
--

CREATE TABLE IF NOT EXISTS `medline_grant` (
  `pmid` int(11) NOT NULL default '0',
  `grant_id` varchar(100) default '',
  `acronym` varchar(100) default NULL,
  `agency` text,
  `country` varchar(100) default NULL,
  UNIQUE KEY `pk_medline_grant` (`pmid`,`grant_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `medline_investigator`
--

CREATE TABLE IF NOT EXISTS `medline_investigator` (
  `pmid` int(11) NOT NULL default '0',
  `last_name` text,
  `fore_name` varchar(255) default NULL,
  `first_name` varchar(255) default NULL,
  `middle_name` varchar(255) default NULL,
  `initials` varchar(100) default NULL,
  `suffix` varchar(100) default NULL,
  `affiliation` text,
  `validyn` varchar(1) default NULL,
  KEY `idx_invest_pmid` (`pmid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `medline_keyword_list`
--

CREATE TABLE IF NOT EXISTS `medline_keyword_list` (
  `pmid` int(11) NOT NULL default '0',
  `keyword` text NOT NULL,
  `keyword_major_yn` char(1) default 'N',
  PRIMARY KEY  (`pmid`,`keyword`(100))
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `medline_mesh_heading`
--

CREATE TABLE IF NOT EXISTS `medline_mesh_heading` (
  `pmid` int(11) NOT NULL default '0',
  `descriptor_name` text NOT NULL,
  `descriptor_name_major_yn` char(1) default 'N',
  PRIMARY KEY  (`pmid`,`descriptor_name`(100))
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `medline_mesh_heading_qualifier`
--

CREATE TABLE IF NOT EXISTS `medline_mesh_heading_qualifier` (
  `pmid` int(11) NOT NULL default '0',
  `descriptor_name` text NOT NULL,
  `qualifier_name` varchar(255) NOT NULL,
  `qualifier_name_major_yn` char(1) default 'N',
  UNIQUE KEY `pk_m_m_h_qualifier` (`pmid`,`descriptor_name`(100),`qualifier_name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `medline_personal_name_subject`
--

CREATE TABLE IF NOT EXISTS `medline_personal_name_subject` (
  `pmid` int(11) NOT NULL default '0',
  `last_name` text,
  `fore_name` varchar(255) default NULL,
  `first_name` varchar(255) default NULL,
  `middle_name` varchar(255) default NULL,
  `initials` varchar(100) default NULL,
  `suffix` varchar(100) default NULL,
  `dates_associated_with_name` text,
  `name_qualifier` text,
  `other_information` text,
  `title_associated_with_name` text,
  KEY `idx_pers_name_pmid` (`pmid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `medline_space_flight_mission`
--

CREATE TABLE IF NOT EXISTS `medline_space_flight_mission` (
  `pmid` int(11) NOT NULL default '0',
  `space_flight_mission` varchar(255) default NULL,
  KEY `idx_flight_pmid` (`pmid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

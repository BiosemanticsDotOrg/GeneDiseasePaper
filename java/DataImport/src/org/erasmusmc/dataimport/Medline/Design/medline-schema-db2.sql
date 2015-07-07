--======================================================================*/
--                         SQL Medline Schema                           */
--                                                                      */
--                           Gaurav Bhalotia				*/
--                            Feb. 16, 2003                             */
--                           Ariel Schwartz                             */
--			      Jun. 10, 2003				*/
--                              Based on:                               */
--                         nlmmedline_021101.dtd                        */
--                      nlmmedlinecitation_021101.dtd                   */
--                         nlmcommon_021101.dtd                         */
--									*/
--	       Revised to support version 031101 of the DTDs		*/
--                         nlmmedline_031101.dtd                        */
--                      nlmmedlinecitation_031101.dtd                   */
--                         nlmcommon_031101.dtd                         */
--			nlmsharedcatcit_031101.dtd			*/
--			     Ariel Schwartz				*/
--			      Mar. 10, 2004				*/
--                                                                      */
--                   The DTD files are located at:                      */
--     		http://www.nlm.nih.gov/databases/dtd/      		*/
--                                                                      */
-- NOTE: Deleted citations are not part of the schema. 			*/
--	 Those should be handled by the code to keep the current 	*/
--       copy updated.							*/
--======================================================================*/


--==============================================================*/
-- TABLE: medline_citation                                      */
--==============================================================*/
DROP TABLE medline_citation;
CREATE TABLE medline_citation(
        pmid                            INTEGER NOT NULL,
        date_created                    DATE NOT NULL,
        date_completed                  DATE,
        date_revised                    DATE,

	--info relating to the article
        issn	                      	CHAR(9),
        volume                          VARCHAR(100),
        issue                           VARCHAR(100),
        pub_date_year                   VARCHAR(4),
        pub_date_month                  VARCHAR(20),
        pub_date_day                    VARCHAR(2),
        pub_date_season                 VARCHAR(10),
        medline_date                    VARCHAR(100),
	journal_print_yn		CHAR(1),
        coden                           VARCHAR(100),
        journal_title                   VARCHAR(2000),
        iso_abbreviation                VARCHAR(50),        
	article_title                   VARCHAR(1000)   NOT NULL,
        start_page                      VARCHAR(10),
        end_page                        VARCHAR(10),
        medline_pgn                     VARCHAR(100),
        abstract_text                   CLOB(10000) NOT LOGGED COMPACT,
        copyright_info                  VARCHAR(1000),
        article_affiliation             VARCHAR(500),
        article_author_list_comp_yn     CHAR(1) DEFAULT 'Y',
        data_bank_list_comp_yn     	CHAR(1) DEFAULT 'Y',
        grantlist_complete_yn           CHAR(1) DEFAULT 'Y',
        vernacular_title                VARCHAR(1000),
        date_of_electronic_publication  DATE,
	elec_pub_official_date_yn	CHAR(1) DEFAULT 'N',
	--end article attributes

	--medline journal info
        country                         VARCHAR(50),
        medline_ta                      VARCHAR(500)    NOT NULL,
        nlm_unique_id                   VARCHAR(20),
	--end medline_journal_info

        xml_file_name                   VARCHAR(500)    NOT NULL,
        number_of_references            INTEGER,
        keyword_list_owner              VARCHAR(30),
        citation_owner                  VARCHAR(30) DEFAULT 'NLM',
        citation_status                 VARCHAR(50)
) in "USERSPACE1" LONG IN "USERSPACELONG";

CREATE UNIQUE INDEX pk_med_citation on medline_citation(pmid) CLUSTER;
ALTER TABLE medline_citation
      ADD CONSTRAINT pk_med_citation PRIMARY KEY (pmid);

--==============================================================*/
-- TABLE: medline_author                                        */
--==============================================================*/
DROP TABLE medline_author;
CREATE TABLE medline_author (
        pmid	                        INTEGER    NOT NULL,
	last_name                       VARCHAR(500),
        fore_name                       VARCHAR(50),
        first_name                      VARCHAR(50),
        middle_name                     VARCHAR(50),
        initials                        VARCHAR(10),   
        suffix                          VARCHAR(10),
        affiliation                     VARCHAR(500),
        collective_name                 VARCHAR(500),
	dates_associated_with_name	VARCHAR(500),
	name_qualifier			VARCHAR(500),
	other_information		VARCHAR(500),
	title_associated_with_name	VARCHAR(500),

        CONSTRAINT fk_med_author
                FOREIGN KEY (pmid) REFERENCES medline_citation (pmid) ON DELETE CASCADE
);
CREATE INDEX idx_author_pmid on medline_author(pmid) CLUSTER;


--==============================================================*/
-- TABLE: medline_chemical_list                                 */
--==============================================================*/
DROP TABLE medline_chemical_list;
CREATE TABLE medline_chemical_list (
        pmid                            INTEGER    NOT NULL,
        registry_number                 VARCHAR(20),   
        name_of_substance               VARCHAR(2000)   NOT NULL,

        CONSTRAINT fk1_med_chem_list
                FOREIGN KEY (pmid)
                        REFERENCES medline_citation (pmid) ON DELETE CASCADE  
);
CREATE INDEX idx_m_chem_pmid on medline_chemical_list(pmid) CLUSTER;

--==============================================================*/
-- TABLE: medline_gene_symbol_list                              */
--==============================================================*/
DROP TABLE medline_gene_symbol_list;
CREATE TABLE medline_gene_symbol_list (
        pmid                            INTEGER    NOT NULL,
        gene_symbol                     VARCHAR(100)    NOT NULL,

        CONSTRAINT fk1_med_gs_list
                FOREIGN KEY (pmid)
                        REFERENCES medline_citation (pmid) ON DELETE CASCADE
);
CREATE UNIQUE INDEX pk_m_g_s_list on medline_gene_symbol_list(pmid, gene_symbol) CLUSTER;
ALTER TABLE medline_gene_symbol_list
      ADD CONSTRAINT pk_m_g_s_list PRIMARY KEY (pmid, gene_symbol);

--==============================================================*/
-- TABLE: medline_keyword_list                                  */
--==============================================================*/
DROP TABLE medline_keyword_list;
CREATE TABLE medline_keyword_list (
        pmid                            INTEGER    NOT NULL,
        keyword                         VARCHAR(500)    NOT NULL,
        keyword_major_yn                CHAR(1)        DEFAULT 'N',

        CONSTRAINT fk1_med_kw_list
                FOREIGN KEY (pmid)
                        REFERENCES medline_citation (pmid) ON DELETE CASCADE
);
CREATE UNIQUE INDEX pk_m_keyword_list on medline_keyword_list(pmid, keyword) CLUSTER;
ALTER TABLE medline_keyword_list
      ADD CONSTRAINT pk_m_keyword_list PRIMARY KEY (pmid, keyword);

--==============================================================*/
-- TABLE: medline_mesh_heading                                  */
--==============================================================*/
DROP TABLE medline_mesh_heading;
CREATE TABLE medline_mesh_heading(
        pmid                            INTEGER    NOT NULL,
        descriptor_name                 VARCHAR(500)   NOT NULL,
        descriptor_name_major_yn        CHAR(1)        DEFAULT 'N',

        CONSTRAINT fk_med_meshheading
                FOREIGN KEY (pmid)
                        REFERENCES medline_citation (pmid) ON DELETE CASCADE
);
CREATE UNIQUE INDEX pk_med_meshheading on medline_mesh_heading(pmid, descriptor_name) CLUSTER;
ALTER TABLE medline_mesh_heading
      ADD CONSTRAINT pk_med_meshheading PRIMARY KEY (pmid, descriptor_name);

--==============================================================*/
-- TABLE: medline_mesh_heading_qualifier                        */
--==============================================================*/
DROP TABLE medline_mesh_heading_qualifier;
CREATE TABLE medline_mesh_heading_qualifier(
        pmid                            INTEGER    NOT NULL,
        descriptor_name                 VARCHAR(500)   NOT NULL,
        qualifier_name                  VARCHAR(50)   NOT NULL,
        qualifier_name_major_yn         CHAR(1)        DEFAULT 'N',

        CONSTRAINT fk_med_mesh_qual
                FOREIGN KEY (pmid, descriptor_name)
                        REFERENCES medline_mesh_heading (pmid, descriptor_name) ON DELETE CASCADE

);
CREATE UNIQUE INDEX pk_m_m_h_qualifier on medline_mesh_heading_qualifier(pmid, descriptor_name, qualifier_name) CLUSTER;
ALTER TABLE medline_mesh_heading_qualifier
      ADD CONSTRAINT pk_m_m_h_qualifier PRIMARY KEY (pmid, descriptor_name, qualifier_name);

--==============================================================*/
-- TABLE: medline_comments_corrections                          */
--==============================================================*/
DROP TABLE medline_comments_corrections;
CREATE TABLE medline_comments_corrections (
        pmid                            INTEGER     NOT NULL,
        ref_pmid                        INTEGER,
        note                            VARCHAR(2000),
        type                            VARCHAR(20),
        ref_source                      VARCHAR(1000),

        CONSTRAINT fk1_med_cc
                FOREIGN KEY (pmid)
                        REFERENCES medline_citation (pmid) ON DELETE CASCADE
);
CREATE INDEX idx_comments_pmid on medline_comments_corrections(pmid) CLUSTER;

--==============================================================*/
-- TABLE: medline_citation_subsets                              */
--==============================================================*/
DROP TABLE medline_citation_subsets;
CREATE TABLE medline_citation_subsets(          
        pmid                            INTEGER    NOT NULL,
        citation_subset                 VARCHAR(500)   NOT NULL,

        CONSTRAINT fk1_med_cit_sub
        	FOREIGN KEY (pmid)
                        REFERENCES medline_citation (pmid) ON DELETE CASCADE
);
CREATE UNIQUE INDEX pk_med_cit_sub on medline_citation_subsets(pmid, citation_subset) CLUSTER;
ALTER TABLE medline_citation_subsets
      ADD CONSTRAINT pk_med_cit_sub PRIMARY KEY (pmid, citation_subset);

--==============================================================*/
-- TABLE: medline_article_publication_type                      */
--==============================================================*/
DROP TABLE medline_article_publication_type;
CREATE TABLE medline_article_publication_type(          
        pmid                            INTEGER     NOT NULL,
        publication_type                VARCHAR(100),     

        CONSTRAINT fk1_med_art_ptype
                FOREIGN KEY (pmid)
                        REFERENCES medline_citation (pmid) ON DELETE CASCADE
);
CREATE INDEX idx_pub_type_pmid on medline_article_publication_type(pmid) CLUSTER;

--==============================================================*/
-- TABLE: medline_article_language                              */
--==============================================================*/
DROP TABLE medline_article_language;
CREATE TABLE medline_article_language(          
        pmid                            INTEGER     NOT NULL,
        language                        VARCHAR(100),     

        CONSTRAINT fk1_med_lang
                FOREIGN KEY (pmid)
                        REFERENCES medline_citation (pmid) ON DELETE CASCADE
);
CREATE INDEX idx_lang_pmid on medline_article_language(pmid) CLUSTER;

--==============================================================*/
-- TABLE: medline_grant                                      */
--==============================================================*/
DROP TABLE medline_grant;
CREATE TABLE medline_grant(          
        pmid                            INTEGER     NOT NULL,
        grant_id                        VARCHAR(100)     NOT NULL,     
        acronym                         VARCHAR(100),     
        agency                          VARCHAR(500),

        CONSTRAINT fk1_medline_grant
                FOREIGN KEY (pmid)
                        REFERENCES medline_citation (pmid) ON DELETE CASCADE
);
CREATE UNIQUE INDEX pk_medline_grant on medline_grant(pmid, grant_id) CLUSTER;
ALTER TABLE medline_grant
      ADD CONSTRAINT pk_medline_grant PRIMARY KEY (pmid, grant_id);

--==============================================================*/
-- TABLE: medline_data_bank                                     */
--==============================================================*/
DROP TABLE medline_data_bank;
CREATE TABLE medline_data_bank(          
        pmid                            INTEGER     NOT NULL,
        data_bank_name                  VARCHAR(100)     NOT NULL,     
        accession_number                VARCHAR(100)	NOT NULL,     

        CONSTRAINT fk1_med_d_bank
                FOREIGN KEY (pmid)
                        REFERENCES medline_citation (pmid) ON DELETE CASCADE
);
CREATE INDEX idx_data_bank_pmid on medline_data_bank(pmid) CLUSTER;

--==============================================================*/
-- TABLE: medline_personal_name_subject                         */
--==============================================================*/
DROP TABLE medline_personal_name_subject;
CREATE TABLE medline_personal_name_subject (
        pmid	                        INTEGER    NOT NULL,
	last_name                       VARCHAR(500),
        fore_name                       VARCHAR(50),
        first_name                      VARCHAR(50),
        middle_name                     VARCHAR(50),
        initials                        VARCHAR(10),   
        suffix                          VARCHAR(10),
	dates_associated_with_name	VARCHAR(500),
	name_qualifier			VARCHAR(500),
	other_information		VARCHAR(500),
	title_associated_with_name	VARCHAR(500),

        CONSTRAINT fk_med_p_name
                FOREIGN KEY (pmid) REFERENCES medline_citation (pmid) ON DELETE CASCADE
);
CREATE INDEX idx_pers_name_pmid on medline_personal_name_subject(pmid) CLUSTER;

--==============================================================*/
-- TABLE: medline_citation_other_id                             */
--==============================================================*/
DROP TABLE medline_citation_other_id;
CREATE TABLE medline_citation_other_id(          
        pmid                            INTEGER     NOT NULL,
	source				VARCHAR(100),
        other_id                        VARCHAR(1000),     

        CONSTRAINT fk1_med_o_id
                FOREIGN KEY (pmid)
                        REFERENCES medline_citation (pmid) ON DELETE CASCADE
);
CREATE INDEX idx_other_id_pmid on medline_citation_other_id(pmid) CLUSTER;

--==============================================================*/
-- TABLE: medline_citation_other_abstract                       */
--==============================================================*/
DROP TABLE medline_citation_other_abstract;
CREATE TABLE medline_citation_other_abstract(          
        pmid                            INTEGER     NOT NULL,
	type				VARCHAR(100),
        copyright_info                  VARCHAR(500),
        abstract_text                   CLOB(10000) NOT LOGGED COMPACT,

        CONSTRAINT fk1_med_o_abs
                FOREIGN KEY (pmid)
                        REFERENCES medline_citation (pmid) ON DELETE CASCADE
) in "USERSPACE1" LONG IN "USERSPACELONG";
CREATE INDEX idx_o_abs_pmid on medline_citation_other_abstract(pmid) CLUSTER;

--==============================================================*/
-- TABLE: medline_space_flight_mission                          */
--==============================================================*/
DROP TABLE medline_space_flight_mission;
CREATE TABLE medline_space_flight_mission(          
        pmid                            INTEGER     NOT NULL,
	space_flight_mission		VARCHAR(100),

        CONSTRAINT fk1_med_space
                FOREIGN KEY (pmid)
                        REFERENCES medline_citation (pmid) ON DELETE CASCADE
);
CREATE INDEX idx_flight_pmid on medline_space_flight_mission(pmid) CLUSTER;

--==============================================================*/
-- TABLE: medline_investigator                                  */
--==============================================================*/
DROP TABLE medline_investigator;
CREATE TABLE medline_investigator (
        pmid	                        INTEGER    NOT NULL,
	last_name                       VARCHAR(500),
        fore_name                       VARCHAR(50),
        first_name                      VARCHAR(50),
        middle_name                     VARCHAR(50),
        initials                        VARCHAR(10),   
        suffix                          VARCHAR(10),
        affiliation                     VARCHAR(500),

        CONSTRAINT fk_med_invest
                FOREIGN KEY (pmid) REFERENCES medline_citation (pmid) ON DELETE CASCADE
);
CREATE INDEX idx_invest_pmid on medline_investigator(pmid) CLUSTER;

--==============================================================*/
-- TABLE: medline_general_note                                  */
--==============================================================*/
DROP TABLE medline_general_note;
CREATE TABLE medline_general_note(          
        pmid                            INTEGER     NOT NULL,
	owner				VARCHAR(100),
	general_note			VARCHAR(1000),

        CONSTRAINT fk1_med_g_note
                FOREIGN KEY (pmid)
                        REFERENCES medline_citation (pmid) ON DELETE CASCADE
);
CREATE INDEX idx_gen_note_pmid on medline_general_note(pmid) CLUSTER;

COMMIT;

--=============================================================*/
-- Set permissions and volatile cardinality                    */
--=============================================================*/

--Grant permission to btloader
GRANT  SELECT,INSERT,UPDATE,DELETE ON TABLE medline_citation TO USER BTLOADER;
GRANT  SELECT,INSERT,UPDATE,DELETE ON TABLE medline_author TO USER BTLOADER;
GRANT  SELECT,INSERT,UPDATE,DELETE ON TABLE medline_chemical_list TO USER BTLOADER;
GRANT  SELECT,INSERT,UPDATE,DELETE ON TABLE medline_gene_symbol_list TO USER BTLOADER;
GRANT  SELECT,INSERT,UPDATE,DELETE ON TABLE medline_keyword_list TO USER BTLOADER;
GRANT  SELECT,INSERT,UPDATE,DELETE ON TABLE medline_mesh_heading TO USER BTLOADER;
GRANT  SELECT,INSERT,UPDATE,DELETE ON TABLE medline_mesh_heading_qualifier TO USER BTLOADER;
GRANT  SELECT,INSERT,UPDATE,DELETE ON TABLE medline_comments_corrections TO USER BTLOADER;
GRANT  SELECT,INSERT,UPDATE,DELETE ON TABLE medline_citation_subsets TO USER BTLOADER;
GRANT  SELECT,INSERT,UPDATE,DELETE ON TABLE medline_article_publication_type TO USER BTLOADER;
GRANT  SELECT,INSERT,UPDATE,DELETE ON TABLE medline_article_language TO USER BTLOADER;
GRANT  SELECT,INSERT,UPDATE,DELETE ON TABLE medline_grant TO USER BTLOADER;
GRANT  SELECT,INSERT,UPDATE,DELETE ON TABLE medline_data_bank TO USER BTLOADER;
GRANT  SELECT,INSERT,UPDATE,DELETE ON TABLE medline_personal_name_subject TO USER BTLOADER;
GRANT  SELECT,INSERT,UPDATE,DELETE ON TABLE medline_citation_other_id TO USER BTLOADER;
GRANT  SELECT,INSERT,UPDATE,DELETE ON TABLE medline_citation_other_abstract TO USER BTLOADER;
GRANT  SELECT,INSERT,UPDATE,DELETE ON TABLE medline_space_flight_mission TO USER BTLOADER;
GRANT  SELECT,INSERT,UPDATE,DELETE ON TABLE medline_investigator TO USER BTLOADER;
GRANT  SELECT,INSERT,UPDATE,DELETE ON TABLE medline_general_note TO USER BTLOADER;


--Volatile cardinality to make the optimizer use the index independent of the
--table size
ALTER TABLE medline_citation VOLATILE CARDINALITY;
ALTER TABLE medline_author VOLATILE CARDINALITY;
ALTER TABLE medline_chemical_list VOLATILE CARDINALITY;
ALTER TABLE medline_gene_symbol_list VOLATILE CARDINALITY;
ALTER TABLE medline_keyword_list VOLATILE CARDINALITY;
ALTER TABLE medline_mesh_heading VOLATILE CARDINALITY;
ALTER TABLE medline_mesh_heading_qualifier VOLATILE CARDINALITY;
ALTER TABLE medline_comments_corrections VOLATILE CARDINALITY;
ALTER TABLE medline_citation_subsets VOLATILE CARDINALITY;
ALTER TABLE medline_article_publication_type VOLATILE CARDINALITY;
ALTER TABLE medline_article_language VOLATILE CARDINALITY;
ALTER TABLE medline_grant VOLATILE CARDINALITY;
ALTER TABLE medline_data_bank VOLATILE CARDINALITY;
ALTER TABLE medline_personal_name_subject VOLATILE CARDINALITY;
ALTER TABLE medline_citation_other_id VOLATILE CARDINALITY;
ALTER TABLE medline_citation_other_abstract VOLATILE CARDINALITY;
ALTER TABLE medline_space_flight_mission VOLATILE CARDINALITY;
ALTER TABLE medline_investigator VOLATILE CARDINALITY;
ALTER TABLE medline_general_note VOLATILE CARDINALITY;

--==============================================================*/
--		optional non-clustered INDEXES			*/
--								*/
-- To be applied AFTER all baseline data has been 		*/
-- loaded to the tables       					*/
--==============================================================*/
--CREATE INDEX idx_m_c_filename ON medline_citation (xml_file_name,pmid);
--CREATE INDEX idx_m_c_date ON medline_citation (date_created,pmid);
--CREATE INDEX idx_m_c_journal ON medline_citation (nlm_unique_id,volume,issue,pmid);
--CREATE INDEX m_k_list_kword_idx ON medline_keyword_list (keyword,pmid);
--CREATE INDEX m_heading_d_name ON medline_mesh_heading (descriptor_name,pmid);

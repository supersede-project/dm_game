--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: arguments; Type: TABLE; Schema: public; Owner: supersede; Tablespace: 
--

CREATE TABLE arguments (
    argument_id bigint NOT NULL,
    content text NOT NULL
);


ALTER TABLE arguments OWNER TO "supersede";

--
-- Name: criterias_matrices_data; Type: TABLE; Schema: public; Owner: supersede; Tablespace: 
--

CREATE TABLE criterias_matrices_data (
    criterias_matrix_data_id bigint NOT NULL,
    game_id bigint NOT NULL,
    criteria_row_id bigint NOT NULL,
    criteria_column_id bigint NOT NULL,
    value bigint NOT NULL
);


ALTER TABLE criterias_matrices_data OWNER TO "supersede";

--
-- Name: game_criterias; Type: TABLE; Schema: public; Owner: supersede; Tablespace: 
--

CREATE TABLE game_criterias (
    game_id bigint NOT NULL,
    criteria_id bigint NOT NULL
);


ALTER TABLE game_criterias OWNER TO "supersede";

--
-- Name: game_players; Type: TABLE; Schema: public; Owner: supersede; Tablespace: 
--

CREATE TABLE game_players (
    game_id bigint NOT NULL,
    player_id bigint NOT NULL
);


ALTER TABLE game_players OWNER TO "supersede";

--
-- Name: game_requirements; Type: TABLE; Schema: public; Owner: supersede; Tablespace: 
--

CREATE TABLE game_requirements (
    game_id bigint NOT NULL,
    requirement_id bigint NOT NULL
);


ALTER TABLE game_requirements OWNER TO "supersede";

--
-- Name: games; Type: TABLE; Schema: public; Owner: supersede; Tablespace: 
--

CREATE TABLE games (
    game_id bigint NOT NULL,
    start_time timestamp with time zone NOT NULL,
    title text,
    description text,
    creator_id bigint NOT NULL,
    finished boolean DEFAULT false NOT NULL
);


ALTER TABLE games OWNER TO "supersede";

--
-- Name: games_players_points; Type: TABLE; Schema: public; Owner: supersede; Tablespace: 
--

CREATE TABLE games_players_points (
    game_id bigint NOT NULL,
    user_id bigint NOT NULL,
    points bigint NOT NULL,
    game_player_point_id bigint NOT NULL,
    agreement_index bigint,
    position_in_voting bigint,
    virtual_position bigint
);


ALTER TABLE games_players_points OWNER TO "supersede";

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: supersede
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE hibernate_sequence OWNER TO "supersede";

--
-- Name: judge_acts; Type: TABLE; Schema: public; Owner: supersede; Tablespace: 
--

CREATE TABLE judge_acts (
    judge_act_id bigint NOT NULL,
    requirements_matrix_data_id bigint NOT NULL,
    judge_id bigint,
    voted boolean DEFAULT false NOT NULL,
    voted_time timestamp with time zone
);


ALTER TABLE judge_acts OWNER TO "supersede";

--
-- Name: notifications; Type: TABLE; Schema: public; Owner: supersede; Tablespace: 
--

CREATE TABLE notifications (
    notification_id bigint NOT NULL,
    message text NOT NULL,
    user_id bigint,
    read boolean NOT NULL,
    email_sent boolean NOT NULL,
    creation_time timestamp with time zone NOT NULL,
    link text
);


ALTER TABLE notifications OWNER TO "supersede";

--
-- Name: player_moves; Type: TABLE; Schema: public; Owner: supersede; Tablespace: 
--

CREATE TABLE player_moves (
    player_move_id bigint NOT NULL,
    requirements_matrix_data_id bigint NOT NULL,
    player_id bigint NOT NULL,
    value bigint,
    played boolean DEFAULT false NOT NULL,
    played_time timestamp with time zone
);


ALTER TABLE player_moves OWNER TO "supersede";

--
-- Name: points; Type: TABLE; Schema: public; Owner: supersede; Tablespace: 
--

CREATE TABLE points (
    point_id bigint NOT NULL,
    description text NOT NULL,
    global_points bigint DEFAULT 0 NOT NULL,
    criteria_points bigint DEFAULT 0 NOT NULL
);


ALTER TABLE points OWNER TO "supersede";

--
-- Name: requirements; Type: TABLE; Schema: public; Owner: supersede; Tablespace: 
--

CREATE TABLE requirements (
    requirement_id bigint NOT NULL,
    name text NOT NULL,
    description text
);


ALTER TABLE requirements OWNER TO "supersede";

--
-- Name: requirements_choices; Type: TABLE; Schema: public; Owner: supersede; Tablespace: 
--

CREATE TABLE requirements_choices (
    requirements_choice_id bigint NOT NULL,
    description text,
    value bigint,
    label text
);


ALTER TABLE requirements_choices OWNER TO "supersede";

--
-- Name: requirements_matrices_data; Type: TABLE; Schema: public; Owner: supersede; Tablespace: 
--

CREATE TABLE requirements_matrices_data (
    requirements_matrix_data_id bigint NOT NULL,
    game_id bigint NOT NULL,
    criteria_id bigint NOT NULL,
    requirement_row_id bigint NOT NULL,
    requirement_column_id bigint NOT NULL,
    value bigint NOT NULL
);


ALTER TABLE requirements_matrices_data OWNER TO "supersede";

--
-- Name: users; Type: TABLE; Schema: public; Owner: supersede; Tablespace: 
--

CREATE TABLE users (
    user_id bigint NOT NULL,
    name text NOT NULL
);


ALTER TABLE users OWNER TO "supersede";

--
-- Name: users_criteria_points; Type: TABLE; Schema: public; Owner: supersede; Tablespace: 
--

CREATE TABLE users_criteria_points (
    user_id bigint NOT NULL,
    criteria_id bigint NOT NULL,
    points bigint DEFAULT 0 NOT NULL,
    user_criteria_points_id bigint NOT NULL
);


ALTER TABLE users_criteria_points OWNER TO "supersede";

--
-- Name: users_points; Type: TABLE; Schema: public; Owner: supersede; Tablespace: 
--

CREATE TABLE users_points (
    user_id bigint NOT NULL,
    user_points bigint DEFAULT 0 NOT NULL,
    users_points_id bigint NOT NULL
);


ALTER TABLE users_points OWNER TO "supersede";

--
-- Name: valutation_criterias; Type: TABLE; Schema: public; Owner: supersede; Tablespace: 
--

CREATE TABLE valutation_criterias (
    criteria_id bigint NOT NULL,
    name text NOT NULL,
    description text
);


ALTER TABLE valutation_criterias OWNER TO "supersede";

--
-- Data for Name: arguments; Type: TABLE DATA; Schema: public; Owner: supersede
--

COPY arguments (argument_id, content) FROM stdin;
\.


--
-- Data for Name: criterias_matrices_data; Type: TABLE DATA; Schema: public; Owner: supersede
--

COPY criterias_matrices_data (criterias_matrix_data_id, game_id, criteria_row_id, criteria_column_id, value) FROM stdin;
\.


--
-- Data for Name: game_criterias; Type: TABLE DATA; Schema: public; Owner: supersede
--

COPY game_criterias (game_id, criteria_id) FROM stdin;
\.


--
-- Data for Name: game_players; Type: TABLE DATA; Schema: public; Owner: supersede
--

COPY game_players (game_id, player_id) FROM stdin;
\.


--
-- Data for Name: game_requirements; Type: TABLE DATA; Schema: public; Owner: supersede
--

COPY game_requirements (game_id, requirement_id) FROM stdin;
\.


--
-- Data for Name: games; Type: TABLE DATA; Schema: public; Owner: supersede
--

COPY games (game_id, start_time, title, description, creator_id, finished) FROM stdin;
\.


--
-- Data for Name: games_players_points; Type: TABLE DATA; Schema: public; Owner: supersede
--

COPY games_players_points (game_id, user_id, points, game_player_point_id, agreement_index, position_in_voting, virtual_position) FROM stdin;
\.


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: supersede
--

SELECT pg_catalog.setval('hibernate_sequence', 4142, true);


--
-- Data for Name: judge_acts; Type: TABLE DATA; Schema: public; Owner: supersede
--

COPY judge_acts (judge_act_id, requirements_matrix_data_id, judge_id, voted, voted_time) FROM stdin;
\.


--
-- Data for Name: notifications; Type: TABLE DATA; Schema: public; Owner: supersede
--

COPY notifications (notification_id, message, user_id, read, email_sent, creation_time, link) FROM stdin;
\.


--
-- Data for Name: player_moves; Type: TABLE DATA; Schema: public; Owner: supersede
--

COPY player_moves (player_move_id, requirements_matrix_data_id, player_id, value, played, played_time) FROM stdin;
\.


--
-- Data for Name: points; Type: TABLE DATA; Schema: public; Owner: supersede
--

COPY points (point_id, description, global_points, criteria_points) FROM stdin;
-3	create decision making process	20	0
-2	negotiator action	10	5
-1	opinion provider action	10	5
\.


--
-- Data for Name: requirements; Type: TABLE DATA; Schema: public; Owner: supersede
--

COPY requirements (requirement_id, name, description) FROM stdin;
2768	D	D
2677	F	F
2769	C	C
2767	E	E
4112	Requirement with a medium-length text	asdasd 
\.


--
-- Data for Name: requirements_choices; Type: TABLE DATA; Schema: public; Owner: supersede
--

COPY requirements_choices (requirements_choice_id, description, value, label) FROM stdin;
616	First requirement is strongly preferred to the second requirement.	0	9
617	First requirement is preferred to the second requirement.	1	7
618	First requirement is weakly preferred to the second requirement.	2	5
619	No preference, but the first requirement comes first.	3	3
620	Indifference.	4	1
621	No preference, but the second requirement comes first.	5	3
622	Second requirement is weakly preferred to the first requirement.	6	5
623	Second requirement is preferred to the first requirement.	7	7
624	Second requirement is strongly preferred to the first requirement.	8	9
\.


--
-- Data for Name: requirements_matrices_data; Type: TABLE DATA; Schema: public; Owner: supersede
--

COPY requirements_matrices_data (requirements_matrix_data_id, game_id, criteria_id, requirement_row_id, requirement_column_id, value) FROM stdin;

\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: supersede
--

COPY users (user_id, name) FROM stdin;
0	wp_amin wp_admin
3341	test_game_int test_game_int
\.


--
-- Data for Name: users_criteria_points; Type: TABLE DATA; Schema: public; Owner: supersede
--

COPY users_criteria_points (user_id, criteria_id, points, user_criteria_points_id) FROM stdin;
\.


--
-- Data for Name: users_points; Type: TABLE DATA; Schema: public; Owner: supersede
--

COPY users_points (user_id, user_points, users_points_id) FROM stdin;
\.


--
-- Data for Name: valutation_criterias; Type: TABLE DATA; Schema: public; Owner: supersede
--

COPY valutation_criterias (criteria_id, name, description) FROM stdin;
2685	I	I
2765	A	A
2686	H	H
\.


--
-- Name: arguments_primary_key; Type: CONSTRAINT; Schema: public; Owner: supersede; Tablespace: 
--

ALTER TABLE ONLY arguments
    ADD CONSTRAINT arguments_primary_key PRIMARY KEY (argument_id);


--
-- Name: criterias_matrices_data_primary_key; Type: CONSTRAINT; Schema: public; Owner: supersede; Tablespace: 
--

ALTER TABLE ONLY criterias_matrices_data
    ADD CONSTRAINT criterias_matrices_data_primary_key PRIMARY KEY (criterias_matrix_data_id);


--
-- Name: game_criterias_pkey; Type: CONSTRAINT; Schema: public; Owner: supersede; Tablespace: 
--

ALTER TABLE ONLY game_criterias
    ADD CONSTRAINT game_criterias_pkey PRIMARY KEY (game_id, criteria_id);


--
-- Name: game_players_pkey; Type: CONSTRAINT; Schema: public; Owner: supersede; Tablespace: 
--

ALTER TABLE ONLY game_players
    ADD CONSTRAINT game_players_pkey PRIMARY KEY (player_id, game_id);


--
-- Name: game_requirements_pkey; Type: CONSTRAINT; Schema: public; Owner: supersede; Tablespace: 
--

ALTER TABLE ONLY game_requirements
    ADD CONSTRAINT game_requirements_pkey PRIMARY KEY (game_id, requirement_id);


--
-- Name: games_players_points_primary_key; Type: CONSTRAINT; Schema: public; Owner: supersede; Tablespace: 
--

ALTER TABLE ONLY games_players_points
    ADD CONSTRAINT games_players_points_primary_key PRIMARY KEY (game_player_point_id);


--
-- Name: games_primary_key; Type: CONSTRAINT; Schema: public; Owner: supersede; Tablespace: 
--

ALTER TABLE ONLY games
    ADD CONSTRAINT games_primary_key PRIMARY KEY (game_id);


--
-- Name: judge_acts_primary_key; Type: CONSTRAINT; Schema: public; Owner: supersede; Tablespace: 
--

ALTER TABLE ONLY judge_acts
    ADD CONSTRAINT judge_acts_primary_key PRIMARY KEY (judge_act_id);


--
-- Name: player_moves_primary_key; Type: CONSTRAINT; Schema: public; Owner: supersede; Tablespace: 
--

ALTER TABLE ONLY player_moves
    ADD CONSTRAINT player_moves_primary_key PRIMARY KEY (player_move_id);


--
-- Name: points_primary_key; Type: CONSTRAINT; Schema: public; Owner: supersede; Tablespace: 
--

ALTER TABLE ONLY points
    ADD CONSTRAINT points_primary_key PRIMARY KEY (point_id);


--
-- Name: requirements_matrices_data_primary_key; Type: CONSTRAINT; Schema: public; Owner: supersede; Tablespace: 
--

ALTER TABLE ONLY requirements_matrices_data
    ADD CONSTRAINT requirements_matrices_data_primary_key PRIMARY KEY (requirements_matrix_data_id);


--
-- Name: requirements_primary_key; Type: CONSTRAINT; Schema: public; Owner: supersede; Tablespace: 
--

ALTER TABLE ONLY requirements
    ADD CONSTRAINT requirements_primary_key PRIMARY KEY (requirement_id);


--
-- Name: users_criteria_points_primary_key; Type: CONSTRAINT; Schema: public; Owner: supersede; Tablespace: 
--

ALTER TABLE ONLY users_criteria_points
    ADD CONSTRAINT users_criteria_points_primary_key PRIMARY KEY (user_criteria_points_id);


--
-- Name: users_points_primary_key; Type: CONSTRAINT; Schema: public; Owner: supersede; Tablespace: 
--

ALTER TABLE ONLY users_points
    ADD CONSTRAINT users_points_primary_key PRIMARY KEY (users_points_id);


--
-- Name: users_primary_key; Type: CONSTRAINT; Schema: public; Owner: supersede; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_primary_key PRIMARY KEY (user_id);


--
-- Name: valutation_criterias_primary_key; Type: CONSTRAINT; Schema: public; Owner: supersede; Tablespace: 
--

ALTER TABLE ONLY valutation_criterias
    ADD CONSTRAINT valutation_criterias_primary_key PRIMARY KEY (criteria_id);


--
-- Name: fki_games_fk; Type: INDEX; Schema: public; Owner: supersede; Tablespace: 
--

CREATE INDEX fki_games_fk ON criterias_matrices_data USING btree (game_id);


--
-- Name: fki_games_users_fk; Type: INDEX; Schema: public; Owner: supersede; Tablespace: 
--

CREATE INDEX fki_games_users_fk ON games USING btree (creator_id);


--
-- Name: fki_judge_acts_req_matrix_data_fk; Type: INDEX; Schema: public; Owner: supersede; Tablespace: 
--

CREATE INDEX fki_judge_acts_req_matrix_data_fk ON judge_acts USING btree (requirements_matrix_data_id);


--
-- Name: fki_player_moves_req_matrix_data_fk; Type: INDEX; Schema: public; Owner: supersede; Tablespace: 
--

CREATE INDEX fki_player_moves_req_matrix_data_fk ON player_moves USING btree (requirements_matrix_data_id);


--
-- Name: fki_req_matrix_data_criterias_fk; Type: INDEX; Schema: public; Owner: supersede; Tablespace: 
--

CREATE INDEX fki_req_matrix_data_criterias_fk ON requirements_matrices_data USING btree (criteria_id);


--
-- Name: fki_req_matrix_data_games_fk; Type: INDEX; Schema: public; Owner: supersede; Tablespace: 
--

CREATE INDEX fki_req_matrix_data_games_fk ON requirements_matrices_data USING btree (game_id);


--
-- Name: fki_valutation_criterias_foreign_key; Type: INDEX; Schema: public; Owner: supersede; Tablespace: 
--

CREATE INDEX fki_valutation_criterias_foreign_key ON users_criteria_points USING btree (criteria_id);


--
-- Name: first_requirements_foreign_key; Type: FK CONSTRAINT; Schema: public; Owner: supersede
--

ALTER TABLE ONLY requirements_matrices_data
    ADD CONSTRAINT first_requirements_foreign_key FOREIGN KEY (requirement_row_id) REFERENCES requirements(requirement_id);


--
-- Name: first_valutation_criterias_foreign_key; Type: FK CONSTRAINT; Schema: public; Owner: supersede
--

ALTER TABLE ONLY criterias_matrices_data
    ADD CONSTRAINT first_valutation_criterias_foreign_key FOREIGN KEY (criteria_row_id) REFERENCES valutation_criterias(criteria_id);


--
-- Name: games_fk; Type: FK CONSTRAINT; Schema: public; Owner: supersede
--

ALTER TABLE ONLY criterias_matrices_data
    ADD CONSTRAINT games_fk FOREIGN KEY (game_id) REFERENCES games(game_id);


--
-- Name: games_foreign_key; Type: FK CONSTRAINT; Schema: public; Owner: supersede
--

ALTER TABLE ONLY game_criterias
    ADD CONSTRAINT games_foreign_key FOREIGN KEY (game_id) REFERENCES games(game_id);


--
-- Name: games_foreign_key; Type: FK CONSTRAINT; Schema: public; Owner: supersede
--

ALTER TABLE ONLY game_players
    ADD CONSTRAINT games_foreign_key FOREIGN KEY (game_id) REFERENCES games(game_id);


--
-- Name: games_foreign_key; Type: FK CONSTRAINT; Schema: public; Owner: supersede
--

ALTER TABLE ONLY game_requirements
    ADD CONSTRAINT games_foreign_key FOREIGN KEY (game_id) REFERENCES games(game_id);


--
-- Name: games_foreign_key; Type: FK CONSTRAINT; Schema: public; Owner: supersede
--

ALTER TABLE ONLY games_players_points
    ADD CONSTRAINT games_foreign_key FOREIGN KEY (game_id) REFERENCES games(game_id);


--
-- Name: games_users_fk; Type: FK CONSTRAINT; Schema: public; Owner: supersede
--

ALTER TABLE ONLY games
    ADD CONSTRAINT games_users_fk FOREIGN KEY (creator_id) REFERENCES users(user_id);


--
-- Name: judge_acts_req_matrix_data_fk; Type: FK CONSTRAINT; Schema: public; Owner: supersede
--

ALTER TABLE ONLY judge_acts
    ADD CONSTRAINT judge_acts_req_matrix_data_fk FOREIGN KEY (requirements_matrix_data_id) REFERENCES requirements_matrices_data(requirements_matrix_data_id);


--
-- Name: player_moves_req_matrix_data_fk; Type: FK CONSTRAINT; Schema: public; Owner: supersede
--

ALTER TABLE ONLY player_moves
    ADD CONSTRAINT player_moves_req_matrix_data_fk FOREIGN KEY (requirements_matrix_data_id) REFERENCES requirements_matrices_data(requirements_matrix_data_id);


--
-- Name: req_matrix_data_criterias_fk; Type: FK CONSTRAINT; Schema: public; Owner: supersede
--

ALTER TABLE ONLY requirements_matrices_data
    ADD CONSTRAINT req_matrix_data_criterias_fk FOREIGN KEY (criteria_id) REFERENCES valutation_criterias(criteria_id);


--
-- Name: req_matrix_data_games_fk; Type: FK CONSTRAINT; Schema: public; Owner: supersede
--

ALTER TABLE ONLY requirements_matrices_data
    ADD CONSTRAINT req_matrix_data_games_fk FOREIGN KEY (game_id) REFERENCES games(game_id);


--
-- Name: requirements_foreign_key; Type: FK CONSTRAINT; Schema: public; Owner: supersede
--

ALTER TABLE ONLY game_requirements
    ADD CONSTRAINT requirements_foreign_key FOREIGN KEY (requirement_id) REFERENCES requirements(requirement_id);


--
-- Name: second_requirements_foreign_key; Type: FK CONSTRAINT; Schema: public; Owner: supersede
--

ALTER TABLE ONLY requirements_matrices_data
    ADD CONSTRAINT second_requirements_foreign_key FOREIGN KEY (requirement_column_id) REFERENCES requirements(requirement_id);


--
-- Name: second_valutation_criterias_foreign_key; Type: FK CONSTRAINT; Schema: public; Owner: supersede
--

ALTER TABLE ONLY criterias_matrices_data
    ADD CONSTRAINT second_valutation_criterias_foreign_key FOREIGN KEY (criteria_column_id) REFERENCES valutation_criterias(criteria_id);


--
-- Name: users_foreign_key; Type: FK CONSTRAINT; Schema: public; Owner: supersede
--

ALTER TABLE ONLY users_criteria_points
    ADD CONSTRAINT users_foreign_key FOREIGN KEY (user_id) REFERENCES users(user_id);


--
-- Name: users_foreign_key; Type: FK CONSTRAINT; Schema: public; Owner: supersede
--

ALTER TABLE ONLY users_points
    ADD CONSTRAINT users_foreign_key FOREIGN KEY (user_id) REFERENCES users(user_id);


--
-- Name: users_foreign_key; Type: FK CONSTRAINT; Schema: public; Owner: supersede
--

ALTER TABLE ONLY game_players
    ADD CONSTRAINT users_foreign_key FOREIGN KEY (player_id) REFERENCES users(user_id);


--
-- Name: users_foreign_key; Type: FK CONSTRAINT; Schema: public; Owner: supersede
--

ALTER TABLE ONLY player_moves
    ADD CONSTRAINT users_foreign_key FOREIGN KEY (player_id) REFERENCES users(user_id);


--
-- Name: users_foreign_key; Type: FK CONSTRAINT; Schema: public; Owner: supersede
--

ALTER TABLE ONLY judge_acts
    ADD CONSTRAINT users_foreign_key FOREIGN KEY (judge_id) REFERENCES users(user_id);


--
-- Name: users_foreign_key; Type: FK CONSTRAINT; Schema: public; Owner: supersede
--

ALTER TABLE ONLY games_players_points
    ADD CONSTRAINT users_foreign_key FOREIGN KEY (user_id) REFERENCES users(user_id);


--
-- Name: valutation_criterias_foreign_key; Type: FK CONSTRAINT; Schema: public; Owner: supersede
--

ALTER TABLE ONLY users_criteria_points
    ADD CONSTRAINT valutation_criterias_foreign_key FOREIGN KEY (criteria_id) REFERENCES valutation_criterias(criteria_id);


--
-- Name: valutation_criterias_foreign_key; Type: FK CONSTRAINT; Schema: public; Owner: supersede
--

ALTER TABLE ONLY game_criterias
    ADD CONSTRAINT valutation_criterias_foreign_key FOREIGN KEY (criteria_id) REFERENCES valutation_criterias(criteria_id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--


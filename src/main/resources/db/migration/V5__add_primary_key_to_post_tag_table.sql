ALTER TABLE post_tag ALTER post_id SET NOT NULL;
ALTER TABLE post_tag ALTER tag_id SET NOT NULL;

ALTER TABLE post_tag ADD PRIMARY KEY (post_id, tag_id);
// CommentPage.jsx
import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import '../styles/CommentPageStyle.css';

const CommentPage = () => {
    const [isModalOpen, setIsModalOpen] = useState(false);

    const openModal = () => setIsModalOpen(true);
    const closeModal = () => setIsModalOpen(false);

    return (
        <div>
            <div className="discussion-box">
                <div className="title-and-accprofile">
                    <h2 className="title">Teatime & Trending Topics - April 24, 2024</h2>
                    <div className="account">
                        <span className="material-symbols-outlined">account_circle</span>
                        <p>Acount_Username</p>
                    </div>
                    <p className="content">
                        Talk about anything, music related or not. However, pop music gossip should be discussed in the Teatime & Trending Topics threads, linked below.
                        Please be respectful; normal rules still apply. Any comments found breaking the rules will be removed and you will be warned or banned.
                    </p>
                    <div className="buttonAndCount">
                        <div className="emotes">
                            <button className="like1">
                                <span className="material-symbols-outlined">favorite</span>
                            </button>
                            <p className="likeCount">11,000</p>
                            <button className="chat1" onClick={openModal}>
                                <span className="material-symbols-outlined">chat_bubble</span>
                            </button>
                            <button className="share1">
                                <span className="material-symbols-outlined">reply_all</span>
                            </button>
                        </div>
                    </div>
                    <p className="createdAt">37m ago</p>
                </div>
            </div>
            <div id="postOutput" className="newpost">
                {/* Display created posts here */}
            </div>

            <section className="replies">
                <div className="title-and-accprofile1">
                    <div className="account">
                        <span className="material-symbols-outlined">account_circle</span>
                        <p className="accname">@username123</p>
                    </div>
                    <p className="reply-desc">
                        On 16 Carriages by Beyonce, at first I heard "only got nose" instead of "only God knows".
                        I know this is very dumb but I still find it very hilarious especially in the more mature context of the song.
                    </p>
                </div>
                <div className="emotes-replies">
                    <button className="like">
                        <span className="material-symbols-outlined">favorite</span>
                    </button>
                    <p className="likeCount">11,000</p>
                    <button className="chat" onClick={openModal}>
                        <span className="material-symbols-outlined">chat_bubble</span>
                    </button>
                    <button className="share">
                        <span className="material-symbols-outlined">reply_all</span>
                    </button>
                </div>
            </section>

            <section className="replies">
                <div className="title-and-accprofile1">
                    <div className="account">
                        <span className="material-symbols-outlined">account_circle</span>
                        <p className="accname">@ilovemusicsss</p>
                    </div>
                    <p className="reply-desc">
                        In Olivia Rodrigo’s Obsessed, I thought she says “I know about her”
                    </p>
                </div>
                <div className="emotes-replies">
                    <button className="like">
                        <span className="material-symbols-outlined">favorite</span>
                    </button>
                    <p className="likeCount">11,000</p>
                    <button className="chat" onClick={openModal}>
                        <span className="material-symbols-outlined">chat_bubble</span>
                    </button>
                    <button className="share">
                        <span className="material-symbols-outlined">reply_all</span>
                    </button>
                </div>
            </section>

            <section className="replies">
                <div className="title-and-accprofile1">
                    <div className="account">
                        <span className="material-symbols-outlined">account_circle</span>
                        <p className="accname">@lanadelreysdaughter</p>
                    </div>
                    <p className="reply-desc">
                        Lana announced her country album Lasso will be dropping this September!!!
                        Yes, I'm so excited for new Lana!!
                    </p>
                </div>
                <div className="emotes-replies">
                    <button className="like">
                        <span className="material-symbols-outlined">favorite</span>
                    </button>
                    <p className="likeCount">11,000</p>
                    <button className="chat" onClick={openModal}>
                        <span className="material-symbols-outlined">chat_bubble</span>
                    </button>
                    <button className="share">
                        <span className="material-symbols-outlined">reply_all</span>
                    </button>
                </div>
            </section>

            {isModalOpen && (
                <div id="postModal" className="modal">
                    <div className="modal-content">
                        <h3>Comment</h3>
                        <form id="postForm">
                            <label htmlFor="content">Write something...</label><br />
                            <textarea id="content" name="content" rows="4" cols="50"></textarea><br />
                            <button type="submit" className="btn-create-post">Create Post</button>
                            <button type="button" className="btn-cancel" onClick={closeModal}>Cancel</button>
                        </form>
                    </div>
                </div>
            )}

            <Link to="/home" className="homebutton">
                <div className="add-discussion-position">
                    <button className="add-discussion">
                        <p className="createpost">HOME</p>
                        <span className="material-symbols-outlined">home</span>
                    </button>
                </div>
            </Link>

            <Link to="/explore" className="explorebutton">
                <div className="add-discussion-position">
                    <button className="add-discussion">
                        <p className="createpost">EXPLORE</p>
                        <span className="material-symbols-outlined">public</span>
                    </button>
                </div>
            </Link>
        </div>
    );
};

export default CommentPage;

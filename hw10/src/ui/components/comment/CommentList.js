import React, { useState, useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';

const CommentList = () => {
    const { bookId } = useParams();
    const [comments, setComments] = useState([]);
    const [newComment, setNewComment] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!bookId || bookId === 'undefined') {
            setError('Invalid book ID');
            setLoading(false);
            return;
        }

        fetch(`/api/v1/book/comments/${bookId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to fetch comments');
                }
                return response.json();
            })
            .then(data => {
                setComments(Array.isArray(data) ? data : []);
                setLoading(false);
            })
            .catch(err => {
                setError(err.message);
                setLoading(false);
            });
    }, [bookId]);

    const handleAddComment = (e) => {
        e.preventDefault();
        if (!newComment.trim()) {
            setError('Comment cannot be empty');
            return;
        }

        const formData = new FormData();
        formData.append('message', newComment);

        fetch(`/api/v1/book/comments/${bookId}`, {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => {
                        throw new Error(err.message || 'Failed to add comment');
                    });
                }
                return fetch(`/api/v1/book/comments/${bookId}`);
            })
            .then(response => response.json())
            .then(data => {
                setComments(Array.isArray(data) ? data : []);
                setNewComment('');
                setError('');
            })
            .catch(err => {
                setError(err.message);
            });
    };

    const handleDeleteComment = (commentId) => {
        if (!window.confirm('Are you sure you want to delete this comment?')) {
            return;
        }

        fetch(`/api/v1/book/comments/${commentId}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => {
                        throw new Error(err.message || 'Failed to delete comment');
                    });
                }
                setComments(comments.filter(comment => comment.id !== commentId));
                setError('');
            })
            .catch(err => {
                setError(err.message);
            });
    };

    return (
        <div style={{
            fontFamily: "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif",
            backgroundColor: '#f4f6f8',
            margin: 0,
            padding: '40px'
        }}>
            <h2 style={{ textAlign: 'center', color: '#2c3e50', marginBottom: '30px' }}>
                Comments for Book ID: {bookId || 'Unknown'}
            </h2>

            {error && (
                <div style={{ color: 'red', textAlign: 'center' }}>
                    <p>{error}</p>
                </div>
            )}

            {loading ? (
                <p style={{ textAlign: 'center' }}>Loading...</p>
            ) : (
                <div style={{ width: '90%', margin: 'auto' }}>
                    <table style={{
                        borderCollapse: 'collapse',
                        width: '100%',
                        backgroundColor: 'white',
                        boxShadow: '0 2px 6px rgba(0, 0, 0, 0.05)'
                    }}>
                        <thead>
                        <tr>
                            <th style={{
                                border: '1px solid #ddd',
                                padding: '12px',
                                textAlign: 'left',
                                backgroundColor: '#ecf0f1',
                                color: '#333'
                            }}>Comment</th>
                            <th style={{
                                border: '1px solid #ddd',
                                padding: '12px',
                                textAlign: 'left',
                                backgroundColor: '#ecf0f1',
                                color: '#333'
                            }}>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {comments.map(comment => (
                            <tr key={comment.id}>
                                <td style={{ border: '1px solid #ddd', padding: '12px' }}>
                                    {comment.message}
                                </td>
                                <td style={{ border: '1px solid #ddd', padding: '12px' }}>
                                    <Link
                                        to={`/book/comment/edit?commentId=${comment.id}&bookId=${bookId}`}
                                        style={{
                                            display: 'inline-block',
                                            padding: '10px 20px',
                                            margin: '5px 0',
                                            color: 'white',
                                            textDecoration: 'none',
                                            borderRadius: '6px',
                                            backgroundColor: '#2980b9',
                                            transition: 'background-color 0.3s ease'
                                        }}
                                        onMouseOver={e => e.currentTarget.style.backgroundColor = '#216a94'}
                                        onMouseOut={e => e.currentTarget.style.backgroundColor = '#2980b9'}
                                    >
                                        Edit
                                    </Link>
                                    <button
                                        style={{
                                            display: 'inline-block',
                                            padding: '10px 20px',
                                            margin: '5px 0',
                                            color: 'white',
                                            border: 'none',
                                            borderRadius: '6px',
                                            backgroundColor: '#c0392b',
                                            cursor: 'pointer',
                                            transition: 'background-color 0.3s ease'
                                        }}
                                        onMouseOver={e => e.currentTarget.style.backgroundColor = '#a93226'}
                                        onMouseOut={e => e.currentTarget.style.backgroundColor = '#c0392b'}
                                        onClick={() => handleDeleteComment(comment.id)}
                                    >
                                        Delete
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}

            <h3 style={{ textAlign: 'center', color: '#2c3e50', marginBottom: '30px' }}>
                Add New Comment
            </h3>
            <form onSubmit={handleAddComment}>
                <textarea
                    name="message"
                    rows="4"
                    style={{ width: '100%', padding: '10px' }}
                    placeholder="Enter your comment"
                    value={newComment}
                    onChange={(e) => setNewComment(e.target.value)}
                    required
                />
                <div style={{ marginTop: '25px', textAlign: 'center' }}>
                    <button
                        type="submit"
                        style={{
                            display: 'inline-block',
                            padding: '10px 20px',
                            margin: '5px 0',
                            color: 'white',
                            border: 'none',
                            borderRadius: '6px',
                            backgroundColor: '#2ecc71',
                            cursor: 'pointer',
                            transition: 'background-color 0.3s ease'
                        }}
                        onMouseOver={e => e.currentTarget.style.backgroundColor = '#27ae60'}
                        onMouseOut={e => e.currentTarget.style.backgroundColor = '#2ecc71'}
                    >
                        Submit
                    </button>
                    <Link
                        to="/"
                        style={{
                            display: 'inline-block',
                            padding: '10px 20px',
                            margin: '5px 0',
                            color: '#2c3e50',
                            textDecoration: 'none',
                            borderRadius: '6px',
                            backgroundColor: '#bdc3c7',
                            transition: 'background-color 0.3s ease'
                        }}
                        onMouseOver={e => e.currentTarget.style.backgroundColor = '#95a5a6'}
                        onMouseOut={e => e.currentTarget.style.backgroundColor = '#bdc3c7'}
                    >
                        Cancel
                    </Link>
                </div>
            </form>

            <div style={{ marginTop: '25px', textAlign: 'center' }}>
                <Link
                    to="/"
                    style={{
                        display: 'inline-block',
                        padding: '10px 20px',
                        margin: '5px 0',
                        color: '#2c3e50',
                        textDecoration: 'none',
                        borderRadius: '6px',
                        backgroundColor: '#bdc3c7',
                        transition: 'background-color 0.3s ease'
                    }}
                    onMouseOver={e => e.currentTarget.style.backgroundColor = '#95a5a6'}
                    onMouseOut={e => e.currentTarget.style.backgroundColor = '#bdc3c7'}
                >
                    Back to Books
                </Link>
            </div>
        </div>
    );
};

export default CommentList;
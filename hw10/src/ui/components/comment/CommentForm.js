import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';

const CommentForm = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const commentId = queryParams.get('commentId');
    const bookId = queryParams.get('bookId');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(!!commentId);

    useEffect(() => {
        if (!commentId || !bookId) {
            setError('Invalid comment or book ID');
            setLoading(false);
            return;
        }

        // Assume an endpoint to fetch comment by ID
        fetch(`/api/v1/book/comments/one/${commentId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to fetch comment');
                }
                return response.json();
            })
            .then(data => {
                setMessage(data.message || '');
                setLoading(false);
            })
            .catch(err => {
                setError(err.message);
                setLoading(false);
            });
    }, [commentId, bookId]);

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!message.trim()) {
            setError('Comment cannot be empty');
            return;
        }

        const formData = new FormData();
        formData.append('message', message);

        fetch(`/api/v1/book/comments/${commentId}`, {
            method: 'PUT',
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => {
                        throw new Error(err.message || 'Failed to update comment');
                    });
                }
                navigate(`/book/comments/${bookId}`);
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
                Edit Comment
            </h2>

            {error && (
                <div style={{ color: 'red', textAlign: 'center' }}>
                    <p>{error}</p>
                </div>
            )}

            {loading ? (
                <p style={{ textAlign: 'center' }}>Loading...</p>
            ) : (
                <form onSubmit={handleSubmit}>
                    <textarea
                        name="message"
                        rows="4"
                        style={{ width: '100%', padding: '10px' }}
                        placeholder="Enter your comment"
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
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
                            Save
                        </button>
                        <Link
                            to={`/book/comments/${bookId}`}
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
            )}
        </div>
    );
};

export default CommentForm;